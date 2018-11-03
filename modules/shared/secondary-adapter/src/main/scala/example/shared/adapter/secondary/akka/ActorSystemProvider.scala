package example.shared.adapter.secondary.akka

import com.google.inject.Provider

import akka.actor.ActorSystem
import example.shared.adapter.config.SharedAdapterConf
import javax.inject.Singleton

@Singleton
class ActorSystemProvider extends Provider[ActorSystem] {
  val actorSystemName: String = SharedAdapterConf.Akka.actorSystemName

  override def get(): ActorSystem = ActorSystem.create(actorSystemName)
}
