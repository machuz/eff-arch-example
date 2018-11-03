package example.akkaHttp
import akka.actor.{ Actor, ActorLogging }

abstract class AbstractAkkaHttpActor extends Actor with ActorLogging {

  def preStart(): Unit

  def postStop(): Unit

  def receive: Receive

}
