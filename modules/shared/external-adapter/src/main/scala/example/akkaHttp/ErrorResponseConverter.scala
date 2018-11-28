package example.akkaHttp
import com.google.inject.Inject
import akka.http.scaladsl.model.{ HttpEntity, HttpResponse, StatusCode }
import akka.http.scaladsl.server.Directives.complete
import akka.http.scaladsl.server.StandardRoute
import example.shared.adapter.secondary.json.circe.JsonPrinter
import example.shared.lib.dddSupport.Error.FormValidationError
import example.shared.lib.dddSupport.ErrorCode

class ErrorResponseConverter @Inject()(
  jsonPrinter: JsonPrinter
) {

  def convertToErrorResponse(statusCode: StatusCode, errorCode: ErrorCode, message: String): HttpEntity.Strict = {
    HttpEntity(
      jsonPrinter.print(ErrorResponse(errorCode, message))
    )
  }

  def convertToErrorResponse(statusCode: StatusCode, errorCode: ErrorCode, throwable: Throwable): StandardRoute = {
    val httpRes = HttpResponse(
      status = statusCode,
      entity = HttpEntity(
        jsonPrinter.print(ErrorResponse(errorCode, throwable.getMessage))
      )
    )
    complete(httpRes)
  }

  def convertToErrorResponse2(statusCode: StatusCode, errorCode: ErrorCode, throwable: Throwable): StandardRoute = {
    val httpRes = HttpResponse(
      status = statusCode,
      entity = HttpEntity(
        jsonPrinter.print(ErrorResponse(errorCode, throwable.getMessage))
      )
    )
    complete(httpRes)
  }

}

class FormErrorResponseConverter @Inject()(
  jsonPrinter: JsonPrinter
) {

  def convertToErrorResponse(statusCode: StatusCode, formValidationError: FormValidationError): StandardRoute = {
    val httpRes = HttpResponse(
      status = statusCode,
      entity = HttpEntity(
        jsonPrinter.print(FormErrorResponse(formValidationError))
      )
    )
    complete(httpRes)
  }

}
