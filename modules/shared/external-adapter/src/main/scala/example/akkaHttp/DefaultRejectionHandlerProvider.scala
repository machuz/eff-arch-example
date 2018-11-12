package example.akkaHttp

import com.google.inject.Provider

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes.{
  BadRequest,
  Forbidden,
  InternalServerError,
  MethodNotAllowed,
  NotAcceptable,
  NotFound,
  RequestedRangeNotSatisfiable,
  ServiceUnavailable,
  Unauthorized,
  UnsupportedMediaType
}
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.server.AuthenticationFailedRejection.{ CredentialsMissing, CredentialsRejected }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{
  AuthenticationFailedRejection,
  AuthorizationFailedRejection,
  CircuitBreakerOpenRejection,
  ExpectedWebSocketRequestRejection,
  InvalidOriginRejection,
  InvalidRequiredValueForQueryParamRejection,
  MalformedFormFieldRejection,
  MalformedHeaderRejection,
  MalformedQueryParamRejection,
  MalformedRequestContentRejection,
  MethodRejection,
  MissingCookieRejection,
  MissingFormFieldRejection,
  MissingHeaderRejection,
  MissingQueryParamRejection,
  RejectionHandler,
  RequestEntityExpectedRejection,
  SchemeRejection,
  TooManyRangesRejection,
  UnacceptedResponseContentTypeRejection,
  UnacceptedResponseEncodingRejection,
  UnsatisfiableRangeRejection,
  UnsupportedRequestContentTypeRejection,
  UnsupportedRequestEncodingRejection,
  ValidationRejection
}
import example.shared.adapter.secondary.json.circe.JsonPrinter
import example.shared.lib.dddSupport.ErrorCode
import javax.inject.Inject

class DefaultRejectionHandlerProvider @Inject()(
  jsonPrinter: JsonPrinter
) extends Provider[RejectionHandler] {

  private def convertToErrorEntity(
    statusCode: StatusCode,
    headers: List[HttpHeader] = Nil,
    errorCode: ErrorCode,
    message: String
  ): HttpResponse = {
    HttpResponse(
      status = statusCode,
      headers = headers,
      entity = HttpEntity(
        jsonPrinter.print(ErrorResponse(errorCode, message))
      )
    )
  }

  private def rejectRequestEntityAndComplete(m: => ToResponseMarshallable) = {
    extractRequest { request =>
      extractMaterializer { implicit mat =>
        request.discardEntityBytes()
        complete(m)
      }
    }
  }

  override def get(): RejectionHandler = {
    RejectionHandler
      .newBuilder()
      .handleAll[SchemeRejection] { rejections =>
        val schemes = rejections.map(_.supported).mkString(", ")
        rejectRequestEntityAndComplete(
          convertToErrorEntity(
            statusCode = BadRequest,
            errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
            message = "Uri scheme not allowed, supported schemes: " + schemes
          )
        )
      }
      .handleAll[MethodRejection] { rejections =>
        val (methods, names) = rejections.map(r => r.supported -> r.supported.name).unzip
        rejectRequestEntityAndComplete("aa")
        rejectRequestEntityAndComplete(
          convertToErrorEntity(
            statusCode = MethodNotAllowed,
            headers = List(Allow(methods)),
            errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
            message = "HTTP method not allowed, supported methods: " + names.mkString(", ")
          )
        )
      }
      .handle {
        case AuthorizationFailedRejection =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = Forbidden,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = "The supplied authentication is not authorized to access this resource"
            )
          )
      }
      .handle {
        case MalformedFormFieldRejection(name, msg, _) =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = BadRequest,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = "The form field '" + name + "' was malformed:\n" + msg
            )
          )
      }
      .handle {
        case MalformedHeaderRejection(headerName, msg, _) =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = BadRequest,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = s"The value of HTTP header '$headerName' was malformed:\n" + msg
            )
          )
      }
      .handle {
        case MalformedQueryParamRejection(name, msg, _) =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = BadRequest,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = "The query parameter '" + name + "' was malformed:\n" + msg
            )
          )
      }
      .handle {
        case MalformedRequestContentRejection(msg, _) =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = BadRequest,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = "The request content was malformed:\n" + msg
            )
          )
      }
      .handle {
        case MissingCookieRejection(cookieName) =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = BadRequest,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = "Request is missing required cookie '" + cookieName + '\''
            )
          )
      }
      .handle {
        case MissingFormFieldRejection(fieldName) =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = BadRequest,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = "Request is missing required form field '" + fieldName + '\''
            )
          )
      }
      .handle {
        case MissingHeaderRejection(headerName) =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = BadRequest,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = "Request is missing required HTTP header '" + headerName + '\''
            )
          )
      }
      .handle {
        case InvalidOriginRejection(allowedOrigins) =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = Forbidden,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = s"Allowed `Origin` header values: ${allowedOrigins.mkString(", ")}"
            )
          )
      }
      .handle {
        case MissingQueryParamRejection(paramName) =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = NotFound,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = "Request is missing required query parameter '" + paramName + '\''
            )
          )
      }
      .handle {
        case InvalidRequiredValueForQueryParamRejection(paramName, requiredValue, _) =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = NotFound,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = s"Request is missing required value '$requiredValue' for query parameter '$paramName'"
            )
          )
      }
      .handle {
        case RequestEntityExpectedRejection =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = BadRequest,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = "Request entity expected but not supplied"
            )
          )
      }
      .handle {
        case TooManyRangesRejection(_) =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = RequestedRangeNotSatisfiable,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = "Request contains too many ranges"
            )
          )
      }
      .handle {
        case CircuitBreakerOpenRejection(_) =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = ServiceUnavailable,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = "Service Unavailable"
            )
          )
      }
      .handle {
        case UnsatisfiableRangeRejection(unsatisfiableRanges, actualEntityLength) =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = RequestedRangeNotSatisfiable,
              headers = List(`Content-Range`(ContentRange.Unsatisfiable(actualEntityLength))),
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message =
                unsatisfiableRanges.mkString("None of the following requested Ranges were satisfiable:\n", "\n", "")
            )
          )
      }
      .handleAll[AuthenticationFailedRejection] { rejections =>
        val rejectionMessage = rejections.head.cause match {
          case CredentialsMissing  => "The resource requires authentication, which was not supplied with the request"
          case CredentialsRejected => "The supplied authentication is invalid"
        }
        rejectRequestEntityAndComplete(
          convertToErrorEntity(
            statusCode = Unauthorized,
            errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
            message = rejectionMessage
          )
        )
      }
      .handleAll[UnacceptedResponseContentTypeRejection] { rejections =>
        val supported = rejections.flatMap(_.supported)
        val msg =
          supported.map(_.format).mkString("Resource representation is only available with these types:\n", "\n", "")
        rejectRequestEntityAndComplete(
          convertToErrorEntity(
            statusCode = NotAcceptable,
            errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
            message = msg
          )
        )
      }
      .handleAll[UnacceptedResponseEncodingRejection] { rejections =>
        val supported = rejections.flatMap(_.supported)
        rejectRequestEntityAndComplete(
          convertToErrorEntity(
            statusCode = NotAcceptable,
            errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
            message = "Resource representation is only available with these Content-Encodings:\n" + supported
              .map(_.value)
              .mkString("\n")
          )
        )
      }
      .handleAll[UnsupportedRequestContentTypeRejection] { rejections =>
        val supported = rejections.flatMap(_.supported).mkString(" or ")
        rejectRequestEntityAndComplete(
          convertToErrorEntity(
            statusCode = UnsupportedMediaType,
            errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
            message = "The request's Content-Type is not supported. Expected:\n" + supported
          )
        )
      }
      .handleAll[UnsupportedRequestEncodingRejection] { rejections =>
        val supported = rejections.map(_.supported.value).mkString(" or ")
        rejectRequestEntityAndComplete(
          convertToErrorEntity(
            statusCode = BadRequest,
            errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
            message = "The request's Content-Encoding is not supported. Expected:\n" + supported
          )
        )
      }
      .handle {
        case ExpectedWebSocketRequestRejection =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = BadRequest,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = "Expected WebSocket Upgrade request"
            )
          )
      }
      .handle {
        case ValidationRejection(msg, _) =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = BadRequest,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = msg
            )
          )
      }
      .handle {
        case x =>
          rejectRequestEntityAndComplete(
            convertToErrorEntity(
              statusCode = InternalServerError,
              errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
              message = "Unhandled rejection: " + x
            )
          )
      }
      .handleNotFound {
        rejectRequestEntityAndComplete(
          convertToErrorEntity(
            statusCode = NotFound,
            errorCode = ErrorCode.AKKA_HTTP_REJECTION_HANDLE_ERROR,
            message = "The requested resource could not be found."
          )
        )
      }
      .result()
  }

}
