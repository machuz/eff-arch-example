package example.shared.adapter.secondary.akka.context

import com.google.inject.{ Inject, Provider }

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext

import javax.inject.Singleton

@Singleton
class DefaultBlockingContextProvider @Inject()(
  actorSystem: ActorSystem
) extends Provider[ExecutionContext] {
  override def get(): ExecutionContext =
    actorSystem.dispatchers.lookup("akka.contexts.default.blocking-dispatcher")
}
