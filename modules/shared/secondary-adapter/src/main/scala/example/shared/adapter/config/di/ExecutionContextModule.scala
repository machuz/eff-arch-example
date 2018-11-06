package example.shared.adapter.config.di

import com.google.inject.AbstractModule
import com.google.inject.name.Names

import example.shared.adapter.secondary.akka.context.{ DefaultAppContextProvider, DefaultBlockingContextProvider }

import scala.concurrent.ExecutionContext

class ExecutionContextModule extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[ExecutionContext])
      .annotatedWith(Names.named("default-app-context"))
      .toProvider(classOf[DefaultAppContextProvider])

    bind(classOf[ExecutionContext])
      .annotatedWith(Names.named("default-blocking-context"))
      .toProvider(classOf[DefaultBlockingContextProvider])
  }

}
