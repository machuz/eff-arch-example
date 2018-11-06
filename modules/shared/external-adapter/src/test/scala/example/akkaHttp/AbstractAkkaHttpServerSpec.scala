package example.akkaHttp

import org.mockito.{ Matchers, Mockito }
import org.scalatest.Assertion
import org.scalatest.concurrent.Eventually

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.{ HttpRequest, StatusCodes }
import akka.http.scaladsl.server.{ HttpApp, Route }
import akka.stream.ActorMaterializer
import example.config.AkkaHttpServerConf
import example.shared.lib.eff.util.idGen.IdGenEffectSpec
import example.shared.lib.test.AbstractSpecification

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, ExecutionContext, Future, Promise }

class AbstractAkkaHttpServerSpec extends AbstractSpecification with RequestBuilding with Eventually {

  import IdGenEffectSpec._
  import Matchers._
  import Mockito._

  trait SetUp {
    val actorSystemName                   = "AbstractAkkaHttpServerSpec"
    implicit val actorSystem: ActorSystem = ActorSystem(actorSystemName)
    implicit val actorMaterializer: ActorMaterializer = ActorMaterializer(
      materializerSettings = None,
      namePrefix = Option(actorSystemName)
    )
    implicit val ec: ExecutionContext = actorSystem.dispatcher

    def withTestServer(testCode: TestServer ⇒ Any): Unit = {
      val mockConf = Mockito.mock(classOf[AkkaHttpServerConf])
      when(mockConf.host).thenReturn("localhost")
      when(mockConf.port).thenReturn(0)
      val testServer = new TestServer(mockConf)

      try testCode(testServer)
      finally {
        if (!testServer.shutdownPromise.isCompleted) testServer.shutdownPromise.success(Done)
      }
    }

    def callAndVerify(binding: ServerBinding, path: String): Assertion = {
      val host = binding.localAddress.getHostString
      val port = binding.localAddress.getPort

      val request  = HttpRequest(uri = s"http://$host:$port/$path")
      val response = Http().singleRequest(request)
      Await.result(response, Duration.Inf).status must be(StatusCodes.OK)
    }
  }

  "AbstractAkkaHttpServer" should {

    "start & shutdown" in new SetUp {
      withTestServer { testServer ⇒
        val server = Future {
          testServer.startServer("localhost", 0)
        }

        val binding = Await.result(testServer.bindingPromise.future, Duration(5, TimeUnit.SECONDS))

        // Checking server is up and running
        callAndVerify(binding, "foo")

        // Requesting the server to shutdown
        testServer.shutdownServer()
        Await.ready(server, Duration(1, TimeUnit.SECONDS))
        server.isCompleted must be(true)
      }
    }
  }

}

class TestServer(override val akkaHttpServerConf: AkkaHttpServerConf)(
  implicit override val actorSystem: ActorSystem,
  implicit override val actorMaterializer: ActorMaterializer,
  implicit override val ec: ExecutionContext
) extends AbstractAkkaHttpServer {

  val shutdownPromise = Promise[Done]()
  val bindingPromise  = Promise[ServerBinding]()

  def shutdownServer(): Unit = shutdownPromise.success(Done)

  override protected def routes: Route =
    path("foo") {
      complete("bar")
    }

  override protected def postHttpBinding(binding: ServerBinding): Unit = {
    super.postHttpBinding(binding)
    bindingPromise.success(binding)
  }

  override protected def waitForShutdownSignal(system: ActorSystem)(implicit ec: ExecutionContext): Future[Done] = {
    shutdownPromise.future
  }
}
