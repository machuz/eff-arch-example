package example

import com.google.inject.Inject

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import example.akkaHttp.AbstractAkkaHttpServer
import example.config.AkkaHttpServerConf
import example.config.di.Injector
import javax.inject.Named

import scala.concurrent.ExecutionContext

object ExampleApiServer extends App with Injector {
  val server = injector.getInstance(classOf[ExampleApiServer])
  server.startServer()
}

class ExampleApiServer @Inject()(
  override val akkaHttpServerConf: AkkaHttpServerConf
)(
  implicit override val actorSystem: ActorSystem,
  override val actorMaterializer: ActorMaterializer,
  @Named("default-app-context") override val ec: ExecutionContext
) extends AbstractAkkaHttpServer {

  override protected def routes: Route =
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    }
}
