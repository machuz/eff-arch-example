package example.shared.adapter.primary.grpc

import org.slf4j.Logger
import org.slf4j.impl.StaticLoggerBinder

object GrpcLogger {

  val binder: StaticLoggerBinder = StaticLoggerBinder.getSingleton
  val logger: Logger             = binder.getLoggerFactory.getLogger("grpc")

}
