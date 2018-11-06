package example
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.google.inject.Inject

import example.akkaHttp.AbstractAkkaHttpRouter
import example.user.UserController

class ExampleRouter @Inject()(userController: UserController) extends AbstractAkkaHttpRouter {

  override def routes: Route = userController.routes

}
