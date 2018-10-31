package example

import akka.actor.ActorSystem

object Dispatchers extends SomeConfig  {
  @volatile private[imp] var currentActorSystem: ActorSystem = _
  def maybeActorSystem: Option[ActorSystem] = Option(currentActorSystem)
  def current = maybeActorSystem.getOrElse(sys.error("set actor system before using it."))

  lazy val router = current.dispatchers.lookup("imp-router-dispatcher")
  lazy val service = current.dispatchers.lookup("imp-service-dispatcher")
  lazy val repository = current.dispatchers.lookup("imp-repository-dispatcher")

}
