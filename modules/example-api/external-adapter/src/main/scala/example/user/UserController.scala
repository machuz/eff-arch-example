package example.user

import akka.http.scaladsl.model.{ ContentTypes, HttpEntity, HttpResponse }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ Route, StandardRoute }
import example.akkaHttp.AbstractAkkaHttpController
import example.exampleApi.usecase.user.create.CreateUserUseCase
import example.exampleApi.usecase.user.show.ShowUserUseCase
import example.user.dto.create.CreateUserRequest
import javax.inject.Inject

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
      post {
        // POST $host/users
        entity(as[CreateUserRequest]) { request =>
          create(request.name)
        }
      } ~
      path(".*".r) { userId: String =>
        get {
          // GET $host/users/${userId}
          show(userId)
        }
      }
    }

  private def index(): StandardRoute =
    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>INDEX</h1>"))

  private def show(userId: String): Route = {
    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>SHOW USER</h1>"))
  }

  private def create(name: Option[String]): Route = {
    complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>CREATE USER</h1>"))
  }

  private def err: StandardRoute = failWith(sys.error("aaa"))
}
