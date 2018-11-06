package example
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import example.akkaHttp.AbstractAkkaHttpRouter

class ExampleController extends AbstractAkkaHttpRouter {

  def showUser: Route = path("yo") {
    get {
      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say YO</h1>"))
    }

  }

}
