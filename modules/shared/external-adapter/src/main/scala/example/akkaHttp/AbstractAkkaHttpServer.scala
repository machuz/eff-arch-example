package example.akkaHttp

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.server.{ HttpApp, Route }
import example.config.AkkaHttpServerConf
import javax.inject.Inject

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Try

abstract class AbstractAkkaHttpServer @Inject()(
  akkaHttpServerConf: AkkaHttpServerConf
)(
  implicit actorSystem: ActorSystem
) extends HttpApp {

  protected val host: String = akkaHttpServerConf.host
  protected val port: Int    = akkaHttpServerConf.port

  protected def routes: Route

  def startServer(): Unit = super.startServer(host, port, actorSystem)

  override protected def postServerShutdown(attempt: Try[Done], system: ActorSystem): Unit = {
    super.postServerShutdown(attempt, system)

    system.terminate()
    Await.result(system.whenTerminated, 30.seconds)
    ()
  }
}
