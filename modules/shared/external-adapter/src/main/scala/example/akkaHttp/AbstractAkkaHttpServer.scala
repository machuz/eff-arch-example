package example.akkaHttp

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.server.{ HttpApp, Route }
import akka.stream.ActorMaterializer
import javax.inject.Inject

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

abstract class AbstractAkkaHttpServer @Inject()(
  implicit actorSystem: ActorSystem,
  actorMaterializer: ActorMaterializer
) extends HttpApp {
  protected def routes: Route
  override protected def postServerShutdown(attempt: Try[Done], system: ActorSystem): Unit = {
    super.postServerShutdown(attempt, system)

    system.terminate()
    Await.result(system.whenTerminated, 30.seconds)
    ()
  }
}
