package example.akkaHttp

import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.util.ByteString

import scala.concurrent.{ ExecutionContext, Future }

abstract class AbstractAkkaHttpController {
  implicit val actorMaterializer: ActorMaterializer
  implicit val ec: ExecutionContext
  def routes: Route

  def extractRequest(
    request: HttpRequest
  ): Future[String] =
    request.entity.dataBytes.runFold(ByteString.empty)(_ ++ _).map(_.utf8String)

}
