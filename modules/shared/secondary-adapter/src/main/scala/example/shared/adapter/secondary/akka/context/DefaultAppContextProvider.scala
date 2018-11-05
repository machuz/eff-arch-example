package example.shared.adapter.secondary.akka.context

import com.google.inject.{ Inject, Provider }

import akka.actor.ActorSystem
import javax.inject.Singleton

import scala.concurrent.ExecutionContext

@Singleton
class DefaultAppContextProvider @Inject()(
  actorSystem: ActorSystem
) extends Provider[ExecutionContext] {
  override def get(): ExecutionContext =
    actorSystem.dispatchers.lookup("akka.contexts.default.app-dispatcher")
}
