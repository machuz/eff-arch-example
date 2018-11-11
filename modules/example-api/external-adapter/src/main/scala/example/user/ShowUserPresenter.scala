package example.user

import akka.http.scaladsl.model.{ HttpEntity, HttpResponse, ResponseEntity, StatusCodes }
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
          entity = HttpEntity(
            jsonPrint(
              obj = UserJsonModel.convertToJsonModel(useCaseRes.user)
            )
          )
        )
        complete(httpRes)
      case -\/(e: Error.UseCaseError) if e.code == ErrorCode.RESOURCE_NOT_FOUND =>
        val httpRes = HttpResponse(
          status = StatusCodes.NotFound,
          entity = HttpEntity(
            jsonPrint(ErrorResponse(e.code, e.getMessage))
          )
        )
        complete(httpRes)
      case -\/(e) =>
        val httpRes = HttpResponse(
          status = StatusCodes.InternalServerError,
          entity = HttpEntity(
            jsonPrint(ErrorResponse(ErrorCode.SERVER_ERROR, e.getMessage))
          )
        )
        complete(httpRes)
    }
  }
}
