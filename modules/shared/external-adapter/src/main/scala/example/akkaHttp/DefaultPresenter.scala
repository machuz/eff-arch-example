package example.akkaHttp

import example.shared.adapter.secondary.json.circe.JsonPrinter
import javax.inject.Inject
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.StandardRoute
import example.shared.lib.dddSupport.{ Error, ErrorCode }

class DefaultPresenter @Inject()(
  errorResponseConverter: ErrorResponseConverter
) {

  def response[A](arg: Either[Error, A]): StandardRoute = {

    arg match {
      case Left(e: Error.UseCaseError) if e.code == ErrorCode.CONFLICT =>
        errorResponseConverter.convertToErrorResponse(StatusCodes.Conflict, e.code, e)
      case Left(e: Error.UseCaseError) if e.code == ErrorCode.UNAUTHORIZED =>
        errorResponseConverter.convertToErrorResponse(StatusCodes.Unauthorized, e.code, e)
      case Left(e: Error.UseCaseError) if e.code == ErrorCode.FORBIDDEN =>
        errorResponseConverter.convertToErrorResponse(StatusCodes.Forbidden, e.code, e)
      case Left(e: Error.UseCaseError) if e.code == ErrorCode.RESOURCE_NOT_FOUND =>
        errorResponseConverter.convertToErrorResponse(StatusCodes.NotFound, e.code, e)
      case Left(e: Error.UseCaseError) if e.code == ErrorCode.SERVER_ERROR =>
        errorResponseConverter.convertToErrorResponse(StatusCodes.InternalServerError, e.code, e)
      case Left(e: Error.FormValidationError) if e.code == ErrorCode.INVALID_FORM_VALUE_ERROR =>
        errorResponseConverter.convertToErrorResponse(StatusCodes.UnprocessableEntity, e.code, e)
      case Left(e) =>
        errorResponseConverter.convertToErrorResponse(StatusCodes.InternalServerError, ErrorCode.SERVER_ERROR, e)
    }
  }

}
