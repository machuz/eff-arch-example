package example.shared.adapter.config.di

import com.google.inject.AbstractModule

import akka.actor.ActorSystem
import example.shared.adapter.secondary.akka.ActorSystemProvider

class ActorSystemModule extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[ActorSystem]).toProvider(classOf[ActorSystemProvider])
  }

}
