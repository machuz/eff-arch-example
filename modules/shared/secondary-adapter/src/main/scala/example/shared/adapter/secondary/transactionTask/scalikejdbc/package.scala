package example.shared.adapter.secondary.transactionTask

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import _root_.scalikejdbc._
import example.shared.lib.transactionTask.{
  ReadTransaction,
  ReadWriteTransaction,
  Transaction,
  TransactionTask,
  TransactionTaskRunner
}

package object scalikejdbc {

  def ask: TransactionTask[Transaction, DBSession] =
    new TransactionTask[Transaction, DBSession] {
      def execute(transaction: Transaction)(implicit ec: ExecutionContext): Future[DBSession] =
        Future.successful(transaction.asInstanceOf[ScalikejdbcTransaction].session)
    }

  implicit def readRunner[R >: ReadTransaction](implicit ec: ExecutionContext): TransactionTaskRunner[R] =
    new TransactionTaskRunner[R] {
      def run[A](task: TransactionTask[R, A]): Future[A] = {
        val session = DB.readOnlySession()
        val future  = task.execute(new ScalikejdbcReadTransaction(session))
        future.onComplete(_ => session.close())
        future
      }
    }

  implicit def readWriteRunner[R >: ReadWriteTransaction](implicit ec: ExecutionContext): TransactionTaskRunner[R] =
    new TransactionTaskRunner[R] {
      def run[A](task: TransactionTask[R, A]): Future[A] = {
        DB.futureLocalTx(session => task.execute(new ScalikejdbcReadWriteTransaction(session)))
      }
    }
}
