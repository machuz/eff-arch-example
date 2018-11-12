package example.config.di
import com.google.inject.AbstractModule

import akka.http.scaladsl.server.RejectionHandler
import example.akkaHttp.{ DefaultPresenter, DefaultRejectionHandlerProvider }

class AkkaHttpServerBuiltInModule extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[DefaultPresenter])
    bind(classOf[RejectionHandler]).toProvider(classOf[DefaultRejectionHandlerProvider])
  }

}
