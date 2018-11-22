package example.shared.adapter.secondary.transactionTask

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import _root_.scalikejdbc._
import cats.data.Reader
import example.shared.lib.eff.db.transactionTask.TransactionTaskTypes
import example.shared.lib.transactionTask.{
  ReadTransaction,
  ReadWriteTransaction,
  Transaction,
  TransactionTask,
  TransactionTask2,
  TransactionTaskRunner
}
import monix.eval.Task

import scala.reflect.ClassTag

package object scalikejdbc {

  def fetchDBSession(a: Transaction): DBSession =
    a.asInstanceOf[ScalikejdbcTransaction].session

//  new TransactionTask2[Transaction] {
//    def execute(transaction: Transaction): Task[DBSession] =
//      Task.now()
//  }

  def sessionAsk: TransactionTask[Transaction, DBSession] =
    new TransactionTask[Transaction, DBSession] {
      def execute(transaction: Transaction): Task[DBSession] =
        Task.now(transaction.asInstanceOf[ScalikejdbcTransaction].session)
    }

  implicit def readRunner[R >: ReadTransaction: ClassTag]: TransactionTaskRunner[R] =
    new TransactionTaskRunner[R] {
      def run[A](task: TransactionTask[R, A]): Task[A] = {
        val session = DB.readOnlySession()
        println("read")
        val s         = new ScalikejdbcReadTransaction(session)
        val monixTask = task.execute(s)
        monixTask
      }
    }

  implicit def readWriteRunner[R >: ReadWriteTransaction]: TransactionTaskRunner[R] =
    new TransactionTaskRunner[R] {
      def run[A](task: TransactionTask[R, A]): Task[A] = {
        println("write")
        DB.localTx(session => task.execute(new ScalikejdbcReadWriteTransaction(session)))
      }
    }
}
