package example.user

import org.atnos.eff.{ Fx, FxAppend }

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpResponse }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ Route, StandardRoute }
import example.akkaHttp.AbstractAkkaHttpController
import example.exampleApi.domain.model.user.UserId
import example.exampleApi.domain.repository.user.UserRepository
import example.exampleApi.usecase.user.create.CreateUserUseCase
import example.exampleApi.usecase.user.show.{ ShowUserUseCase, ShowUserUseCaseArgs }
import example.shared.lib.eff.ErrorEither
import example.shared.lib.eff.db.transactionTask.TransactionTask
import javax.inject.Inject
import example.shared.lib.eff.myEff._
import example.user.dto.create.CreateUserRequest
import monix.eval.Task

class UserController @Inject()(
  showUserPresenter: ShowUserPresenter,
  showUserUseCase: ShowUserUseCase,
  createUserUseCase: CreateUserUseCase,
  userRepo: UserRepository
) extends AbstractAkkaHttpController {

  type R = Fx.fx3[TransactionTask, Task, ErrorEither]

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

  private def show(userId: String): Route = {
    for {
      useCaseRes <- {
        val arg = ShowUserUseCaseArgs(UserId(userId))
        showUserUseCase.execute[R](arg)
      }
    } yield useCaseRes

    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>SHOW USER</h1>"))
  }

  private def create(name: Option[String]): Route = {
    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>CREATE USER</h1>"))
  }

  private def err: StandardRoute = failWith(sys.error("aaa"))
}
