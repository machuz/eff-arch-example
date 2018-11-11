package example.akkaHttp

import akka.http.scaladsl.server.StandardRoute

abstract class AbstractAkkaHttpPresenter[T] {

  def response(arg: T): StandardRoute
}
