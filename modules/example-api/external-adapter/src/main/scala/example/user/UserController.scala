package example.user

import org.atnos.eff.{ Fx, FxAppend }

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpResponse }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ Route, StandardRoute }
import example.akkaHttp.AbstractAkkaHttpController
import example.exampleApi.domain.model.user.UserId
import example.exampleApi.usecase.user.create.CreateUserUseCase
import example.exampleApi.usecase.user.show.{ ShowUserUseCase, ShowUserUseCaseArgs }
import example.user.dto.create.CreateUserRequest
import javax.inject.Inject
import example.shared.lib.eff._

class UserController @Inject()(
  showUserPresenter: ShowUserPresenter,
  showUserUseCase: ShowUserUseCase,
  createUserUseCase: CreateUserUseCase
) extends AbstractAkkaHttpController {

  override def routes: Route =
    path("users") {
      pathEndOrSingleSlash {
        get {
          // GET $host/users
          index()
        }
      } ~
//      post {
//        // POST $host/users
//        entity(as[CreateUserRequest]) { request =>
//          create(request.name)
//        }
//      } ~
      path(".*".r) { userId: String =>
        get {
          // GET $host/users/${userId}
          show(userId)
        }
      }
    }

  private def index(): StandardRoute = ???

  private def show(userId: String): Route = ???
//    {
//    type R = FxAppend[DBStack, Fx.fx1[ErrorEither]]
//    (for {
//      useCaseRes <- {
//        val arg = ShowUserUseCaseArgs(UserId(userId))
//        showUserUseCase.execute[R](arg)
//      }
//    } yield useCaseRes)
//  }

//    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>SHOW USER</h1>"))
//  }

  private def create(name: Option[String]): Route = {
    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>CREATE USER</h1>"))
  }

  private def err: StandardRoute = failWith(sys.error("aaa"))
}
