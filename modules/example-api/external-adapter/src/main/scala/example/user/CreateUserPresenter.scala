package example.user

import example.akkaHttp.{DefaultPresenter, FormErrorResponseConverter}
import example.exampleApi.usecase.user.create.CreateUserUseCaseResult
import example.shared.adapter.secondary.json.circe.JsonPrinter
import javax.inject.Inject
//import akka.http.scaladsl.model.{ HttpEntity, HttpResponse, ResponseEntity, StatusCodes }
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.StandardRoute
import example.akkaHttp.AbstractAkkaHttpPresenter
import example.shared.lib.dddSupport.Error

class CreateUserPresenter @Inject()(
  jsonPrinter: JsonPrinter,
  defaultPresenter: DefaultPresenter,
  formErrorResponseConverter: FormErrorResponseConverter,
) extends AbstractAkkaHttpPresenter[Either[Error, CreateUserUseCaseResult]] {
  override def response(arg: Either[Error, CreateUserUseCaseResult]): StandardRoute = {
    arg match {
      case Right(useCaseRes) =>
        val httpRes = HttpResponse(
          status = StatusCodes.OK,
          entity = HttpEntity(
            jsonPrinter.print(
              obj = UserJsonModel.convertToJsonModel(useCaseRes.user)
            )
          )
        )
        complete(httpRes)
      case Left(e) =>
        val httpRes = HttpResponse(
          status = StatusCodes.BadRequest,
          entity = formErrorResponseConverter.convertToErrorResponse(e)
        )
        complete(httpRes)
    }
  }
}
