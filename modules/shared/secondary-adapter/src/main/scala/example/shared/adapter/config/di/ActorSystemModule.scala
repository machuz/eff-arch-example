package example.shared.adapter.config.di

import com.google.inject.AbstractModule

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import example.shared.adapter.secondary.akka.{ ActorMaterializerProvider, ActorSystemProvider }

class ActorSystemModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[ActorSystem]).toProvider(classOf[ActorSystemProvider])
    bind(classOf[ActorMaterializer]).toProvider(classOf[ActorMaterializerProvider])
  }

}
