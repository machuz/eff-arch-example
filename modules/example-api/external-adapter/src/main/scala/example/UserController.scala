package example
import akka.http.scaladsl.model.HttpHeader
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ Route, StandardRoute }

import scala.concurrent.Future

object Boot extends App {
  new Boot
}

class Boot extends SomeConfig {
  //わかりやすいようにうちわがにimportしてるだけ
  import akka.actor.ActorSystem
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.Http.ServerBinding
  import akka.stream.ActorMaterializer
  import scala.concurrent.Future

  implicit val system: ActorSystem = ActorSystem("payment-api-server")

  Dispatchers.currentActorSystem = system

  val port: Int                                = basicConfig.getInt("payment-api-server.basic.port")
  val interface: String                        = basicConfig.getString("payment-api-server.basic.interface")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val handler = ApiRouter.router

  val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(handler, interface, port)

}

trait AbstractController {}

class UserController(usecase: UserUseCase, presenter: ShowUserPresenter) extends AbstractController with Authenticator {

  def userRoute: Route =
    path("user") {
      //userというパスに対してアクションを設定する
      show() ~ create()
    }

  private[this] def show() = {
    //circeでdecodeするためにimportする
    // see: https://github.com/hseeberger/akka-http-json
    import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
    import io.circe.generic.auto._

    implicit val ec = Dispatchers.router

    get {
      //コンテキストからリクエストを取り出す
      extract(ctx => ctx.request) { request =>
        //Authのイメージ
        authorizeAsync(_ => isAuthenticated(request.headers)) {
          //JSONをUnmarshalする
          entity(as[ShowUserRequest]) { request => presenter.response(usecase.execute())
          }
        }
      }
    }

  }

  private[this] def create() = {
    //circeでdecodeするためにimportする
    // see: https://github.com/hseeberger/akka-http-json
    import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
    import io.circe.generic.auto._
    post {
      //コンテキストからリクエストを取り出す
      extract(ctx => ctx.request) { request =>
        //Authのイメージ
        authorizeAsync(_ => isAuthenticated(request.headers)) {
          //JSONをUnmarshalする
          entity(as[ShowUserRequest]) { request => presenter.response(usecase.execute())
          }
        }
      }

    }
  }

}

trait NextController extends AbstractController {

  def testRoute: Route =
    get {
      path("next") {
        complete("no problem")
      }
    }
}

trait ApiRouter extends UserController with NextController {

  lazy val router = userRoute ~ testRoute

}

trait Authenticator {
  def isAuthenticated(headers: Seq[HttpHeader]): Future[Boolean] = Future.successful(true)
}

trait UseCase {
  def execute(): ApiResponse
}

sealed trait Presenter {
  def response(_with: ApiResponse): StandardRoute
}

trait ShowUserPresenter extends Presenter {
  override def response(_with: ApiResponse): StandardRoute = {
    import io.circe.generic.auto._
    import io.circe.syntax._
    //ここでエラーハンドリングもしなければいけないはず
    complete(_with.asJson.noSpaces)
  }
}

//UserUseCaseInterface??
trait UserUseCase extends UseCase {}


object ApiRouter extends ApiRouter

//Request
sealed trait ApiRequest
case class ShowUserRequest()   extends ApiRequest
case class CreateUserRequest() extends ApiRequest

//Response
sealed trait ApiResponse
case class ShowUserResponse()   extends ApiResponse
case class CreateUserResponse() extends ApiResponse
