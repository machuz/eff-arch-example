package example.user

import akka.http.scaladsl.model.{  HttpResponse, ResponseEntity, StatusCodes }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.StandardRoute
import example.akkaHttp.{ AbstractAkkaHttpPresenter, ErrorResponse }
import example.exampleApi.usecase.user.show.ShowUserUseCaseResult
import example.shared.lib.dddSupport.{ Error, ErrorCode }

import scalaz.{ -\/, \/, \/- }

class ShowUserPresenter extends AbstractAkkaHttpPresenter[\/[Error, ShowUserUseCaseResult]] {
  override def response(arg: \/[Error, ShowUserUseCaseResult]): StandardRoute = {
    arg match {
      case \/-(useCaseRes) =>
        val httpRes = HttpResponse(
          status = StatusCodes.OK,
          entity = ResponseEntity(UserJsonModel.convertToJsonModel(useCaseRes.user).toJson)
        )
        complete(httpRes)
      case -\/(e: Error.UseCaseError) if e.code == ErrorCode.RESOURCE_NOT_FOUND =>
        val httpRes = HttpResponse(
          status = StatusCodes.NotFound,
          entity = ResponseEntity(ErrorResponse(e.code, e.getMessage).toJson)
        )
        complete(httpRes)
      case -\/(e) =>
        val httpRes = HttpResponse(
          status = StatusCodes.InternalServerError,
          entity = ResponseEntity(ErrorResponse(ErrorCode.SERVER_ERROR, e.getMessage).toJson)
        )
        complete(httpRes)
    }
  }
}
