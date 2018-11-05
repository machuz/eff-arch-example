package example.shared.adapter.secondary.akka

import com.google.inject.Provider

import akka.actor.ActorSystem
import example.shared.adapter.config.AkkaConf
import javax.inject.{ Inject, Singleton }

@Singleton
class ActorSystemProvider @Inject()(
  akkaConf: AkkaConf
) extends Provider[ActorSystem] {
  val actorSystemName: String = akkaConf.actorSystemName

  override def get(): ActorSystem = ActorSystem.create(actorSystemName)
}
