package example

import com.google.inject.Inject

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }
import akka.http.scaladsl.server.Route
import example.akkaHttp.AbstractAkkaHttpServer
import example.config.AkkaHttpServerConf
import example.config.di.Injector

object ExampleApiServer extends App with Injector {
  val server = injector.getInstance(classOf[ExampleApiServer])
  server.startServer()
}

class ExampleApiServer @Inject()(
  akkaHttpServerConf: AkkaHttpServerConf
)(implicit actorSystem: ActorSystem)
  extends AbstractAkkaHttpServer(akkaHttpServerConf) {
  override protected def routes: Route =
    path("hello") {
      get {
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
      }
    }
}
