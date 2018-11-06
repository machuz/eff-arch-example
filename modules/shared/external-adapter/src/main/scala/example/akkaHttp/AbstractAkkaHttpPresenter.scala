package example.akkaHttp

import akka.http.scaladsl.server.StandardRoute

abstract class AbstractAkkaHttpPresenter[T] {

  type Rendered = StandardRoute
  type Arg      = T

  def response(arg: Arg): Rendered
}
