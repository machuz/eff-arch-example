package example.akkaHttp

import akka.http.scaladsl.server.StandardRoute
import io.circe.Encoder
import io.circe.syntax._

abstract class AbstractAkkaHttpPresenter[T] {

  def response(arg: T): StandardRoute

  def jsonPrint[A](obj: A)(implicit encoder: Encoder[A]): String = obj.asJson.noSpaces

}
