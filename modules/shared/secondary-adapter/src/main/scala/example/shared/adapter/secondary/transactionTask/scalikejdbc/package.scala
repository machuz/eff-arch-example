package example.shared.adapter.secondary.transactionTask

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import _root_.scalikejdbc._
import example.shared.lib.transactionTask.{ ReadTransaction, ReadWriteTransaction, Task, TaskRunner, Transaction }

package object scalikejdbc {

  def ask: Task[Transaction, DBSession] =
    new Task[Transaction, DBSession] {
      def execute(transaction: Transaction)(implicit ec: ExecutionContext): Future[DBSession] =
        Future.successful(transaction.asInstanceOf[ScalikeJDBCTransaction].session)
    }

  implicit def readRunner[R >: ReadTransaction](implicit ec: ExecutionContext): TaskRunner[R] =
    new TaskRunner[R] {
      def run[A](task: Task[R, A]): Future[A] = {
        val session = DB.readOnlySession()
        val future  = task.execute(new ScalikeJDBCReadTransaction(session))
        future.onComplete(_ => session.close())
        future
      }
    }

  implicit def readWriteRunner[R >: ReadWriteTransaction](implicit ec: ExecutionContext): TaskRunner[R] =
    new TaskRunner[R] {
      def run[A](task: Task[R, A]): Future[A] = {
        DB.futureLocalTx(session => task.execute(new ScalikeJDBCReadWriteTransaction(session)))
      }
    }

}
