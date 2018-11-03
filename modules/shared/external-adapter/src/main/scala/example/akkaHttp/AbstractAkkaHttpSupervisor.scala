package example.akkaHttp

import scala.concurrent.duration.Duration
import scala.util.control.NonFatal

import akka.actor.SupervisorStrategy.Resume
import akka.actor.{ Actor, ActorLogging, OneForOneStrategy, SupervisorStrategy }

abstract class AbstractAkkaHttpSupervisor extends Actor with ActorLogging {

  protected val maxRetries: Int
  protected val timeRange: Duration

  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = maxRetries, withinTimeRange = timeRange) {
      case NonFatal(ex) =>
        log.error(s"supervisor caught error. resume children. error: ${ex.getMessage}")
        Resume
    }

  def receive: Receive

}
