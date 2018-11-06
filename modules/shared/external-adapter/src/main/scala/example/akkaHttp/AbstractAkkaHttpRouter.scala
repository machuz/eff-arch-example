package example.akkaHttp
import akka.http.scaladsl.server.Route

abstract class AbstractAkkaHttpRouter {
  def routes: Route
}
