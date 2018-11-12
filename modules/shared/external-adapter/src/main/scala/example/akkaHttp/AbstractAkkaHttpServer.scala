package example.akkaHttp

import akka.Done
import akka.actor.{ ActorSystem, Terminated }
import akka.http.scaladsl.server.{ HttpApp, RejectionHandler, Route }
import akka.stream.ActorMaterializer
import example.config.AkkaHttpServerConf
import javax.inject.Inject

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext, Future }
import scala.util.{ Failure, Success, Try }

abstract class AbstractAkkaHttpServer @Inject()(
  )
  extends HttpApp {

  implicit val actorSystem: ActorSystem
  implicit val actorMaterializer: ActorMaterializer
  implicit val ec: ExecutionContext
  implicit val rejectionHander: RejectionHandler

  val akkaHttpServerConf: AkkaHttpServerConf

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

  def shutdown(): Future[Terminated] = {
    binding() match {
      case Success(b) =>
        b.terminate(hardDeadline = 3.seconds).flatMap(_ => actorSystem.terminate())
      case Failure(e) => throw e
    }
  }

}
