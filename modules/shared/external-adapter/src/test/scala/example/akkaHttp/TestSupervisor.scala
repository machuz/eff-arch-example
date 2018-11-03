package example.akkaHttp

import scala.concurrent.duration._
import akka.actor.{ Props, Terminated }

class TestSupervisor extends AbstractAkkaHttpSupervisor {

  import TestSupervisor._

  override val maxRetries: Int     = 3
  override val timeRange: Duration = 3.seconds

  override def receive: Receive = {
    case RegistrationCommand(props) =>
      log.info("receive registration command.")
      context.watch(context.actorOf(props, TestActor.name))
      ()
    case cmd: TestActor.ActorCommand =>
      context.child(TestActor.name).foreach(_ forward cmd)
    case Terminated(child) =>
      log.warning(s"terminated child. path: ${child.path}")
    case unknown =>
      log.error(s"receive unknown type. type: ${unknown.getClass.getName}")
  }

}

object TestSupervisor {

  def props(maxRetries: Int, timeRange: Duration) = Props(new TestSupervisor)

  final case class RegistrationCommand(props: Props)

}
