package example.akkaHttp
import akka.actor.{ Actor, ActorLogging }

abstract class AbstractAkkaHttpActor extends Actor with ActorLogging {

  override def preStart(): Unit

  override def postStop(): Unit

  override def receive: Receive

}
