package example

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

class UserController {

  def show(): Route = complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>SHOW USER</h1>"))

}
