package example.akkaHttp

import akka.http.scaladsl.server.Route

abstract class AbstractAkkaHttpController {
  def routes: Route

}
