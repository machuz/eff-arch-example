package example.akkaHttp

import scala.concurrent.duration.Duration
import scala.util.control.NonFatal

import akka.actor.SupervisorStrategy.Resume
import akka.actor.{ Actor, ActorLogging, OneForOneStrategy, SupervisorStrategy }

abstract class AbstractSupervisor(maxRetries: Int, timeRange: Duration) extends Actor with ActorLogging {

  // 例外はログに出して継続
  override def supervisorStrategy: SupervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = maxRetries, withinTimeRange = timeRange) {
      case NonFatal(ex) =>
        log.error(s"supervisor caught error. resume children. error: ${ex.getMessage}")
        Resume
    }

  def receive: Receive

}
