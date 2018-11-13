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
import monix.eval.{ Callback, Task }

package object scalikejdbc {

  def sessionAsk: TransactionTask[Transaction, DBSession] =
    new TransactionTask[Transaction, DBSession] {
      def execute(transaction: Transaction): Task[DBSession] =
        Task.now(transaction.asInstanceOf[ScalikejdbcTransaction].session)
    }

  implicit def readRunner[R >: ReadTransaction]: TransactionTaskRunner[R] =
    new TransactionTaskRunner[R] {
      def run[A](task: TransactionTask[R, A]): Task[A] = {
        val session   = DB.readOnlySession()
        val monixTask = task.execute(new ScalikejdbcReadTransaction(session))
        monixTask
      }
    }

  implicit def readWriteRunner[R >: ReadWriteTransaction]: TransactionTaskRunner[R] =
    new TransactionTaskRunner[R] {
      def run[A](task: TransactionTask[R, A]): Task[A] = {
        DB.localTx(session => task.execute(new ScalikejdbcReadWriteTransaction(session)))
      }
    }
}
