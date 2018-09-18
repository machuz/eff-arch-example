package example.shared.adapter.primary.grpc.intercepter

import io.grpc.{
  ForwardingServerCall,
  ForwardingServerCallListener,
  Metadata,
  PartialForwardingServerCallListener,
  ServerCall,
  ServerCallHandler,
  ServerInterceptor,
  Status,
  StatusRuntimeException
}

import jp.eigosapuri.es.shared.adapter.primary.grpc.GrpcLogger

class ErrorHandleIntercepter extends ServerInterceptor {
  override def interceptCall[ReqT, RespT](
    serverCall: ServerCall[ReqT, RespT],
    headers: Metadata,
    next: ServerCallHandler[ReqT, RespT]
  ): ServerCall.Listener[ReqT] = {
    val listener = next.startCall(serverCall, headers)
    new ForwardingServerCallListener.SimpleForwardingServerCallListener[ReqT](listener) {
      override def onMessage(message: ReqT) {
        try {
          delegate.onMessage(message)
        } catch {
          case e: Exception =>
            closeWithException(e, headers)
        }
      }

      override def onHalfClose() {
        try {
          delegate.onHalfClose()
        } catch {
          case e: Exception =>
            closeWithException(e, headers)
        }
      }
      override def onCancel() {
        try {
          delegate.onCancel()
        } catch {
          case e: Exception =>
            closeWithException(e, headers)
        }
      }

      override def onComplete() {
        try {
          delegate.onComplete()
        } catch {
          case e: Exception =>
            closeWithException(e, headers)
        }
      }

      override def onReady() {
        try {
          delegate.onReady()
        } catch {
          case e: Exception =>
            closeWithException(e, headers)
        }
      }

      private def closeWithException(t: Exception, requestHeader: Metadata) {
        // この段階で持っているserverCallをcloseしてしまうと，streamがその時点で削除される
        // その結果client側では，GRPC::Unknownが出てしまう
        // そのため，ここでExceptionをgrpcのStatusにマッピングしてStatusRuntimeExceptionを投げ直す
        // 投げられたStatusRutnimeExceptionはTransmitStatusRuntimeExceptionInterceptorで拾われる
        var status: Status = null

        t match {
          case _ => status = Status.Code.INTERNAL.toStatus.withDescription(t.getMessage).withCause(t)
        }
        GrpcLogger.logger.error(status.toString)
        throw new StatusRuntimeException(status)
      }
    }
  }
}
