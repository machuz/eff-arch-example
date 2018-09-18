package example.shared.lib.adapter.secondary.repository

import scala.concurrent.{ ExecutionContext, Future }
import scalaz.Scalaz._

import jp.eigosapuri.es.shared.lib.dddSupport.adapter.secondary.repository.DBIOTaskRunner

class TestTaskRunner extends DBIOTaskRunner[Identity] {
  override def run[R](a: Identity[R])(implicit ctx: ExecutionContext): Future[R] = Future.successful(a.value)
  override def runTransaction[R](a: Identity[R])(implicit ctx: ExecutionContext): Future[R] =
    Future.successful(a.value)
}
