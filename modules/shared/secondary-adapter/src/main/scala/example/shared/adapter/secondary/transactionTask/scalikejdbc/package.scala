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

import scala.reflect.ClassTag

package object scalikejdbc {

  def sessionAsk: TransactionTask[Transaction, DBSession] =
    new TransactionTask[Transaction, DBSession] {
      def execute(transaction: Transaction): Task[DBSession] =
        Task.now(transaction.asInstanceOf[ScalikejdbcTransaction].session)
    }

  def isType[R <: Transaction: ClassTag](a: Transaction) = {
    a match {
      case a: Transaction =>
        println("tran")
        true
      case a: ReadTransaction =>
        println("readTran")
        true
      case a: ReadWriteTransaction =>
        println("readWriteTran")
        true
      case _ => false
    }
  }

  implicit def readRunner[R >: ReadTransaction: ClassTag]: TransactionTaskRunner[R] =
    new TransactionTaskRunner[R] {
      def run[A](task: TransactionTask[R, A]): Task[A] = {
        val session   = DB.readOnlySession()
        val monixTask = task.execute(new ScalikejdbcReadTransaction(session))
        monixTask
      }
    }

  implicit def readWriteRunner[R >: ReadWriteTransaction: ClassTag]: TransactionTaskRunner[R] =
    new TransactionTaskRunner[R] {
      def run[A](task: TransactionTask[R, A]): Task[A] = {
        DB.localTx(session => task.execute(new ScalikejdbcReadWriteTransaction(session)))
      }
    }
}
