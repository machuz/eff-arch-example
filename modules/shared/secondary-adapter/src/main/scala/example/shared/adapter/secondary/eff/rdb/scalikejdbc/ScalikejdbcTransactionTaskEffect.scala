package example.shared.adapter.secondary.eff.rdb.scalikejdbc
import org.atnos.eff.{ Eff, IntoPoly, Member }

import cats.data._
import cats.implicits._
import example.shared.adapter.secondary.transactionTask.scalikejdbc.ScalikejdbcDbSession
import example.shared.lib.transactionTask.DbSession
import example.shared.lib.transactionTask.Transaction.{ NoTransaction, ReadTransaction, ReadWriteTransaction }
import monix.eval.Task
import monix.execution.Scheduler
import scalikejdbc.DB

import scala.concurrent.ExecutionContext
//import org.atnos.eff.all._
//import org.atnos.eff.syntax.all._

import example.shared.lib.eff._
import example.shared.lib.eff.atnosEff._
import example.shared.lib.eff.atnosEffSyntax._
//import example.shared.lib.eff.atnosEffCreation._
import example.shared.lib.transactionTask.{ Transaction, TransactionTask }

trait ScalikejdbcTransactionTaskEffect {

  implicit class TranTaskOps[R, A](effects: Eff[R, A]) extends ScalikejdbcInterpreter {
    def runTranTask[U](
      implicit ec: ExecutionContext,
      m1: _task[U],
//      m2: _readerDbSession[U],
//      m4: _stateTransaction[R],
      member1: Member.Aux[TranTask, R, U],
//      member2: Member[ReaderDbSession, R],
      member3: Member[StateTransaction, U],
      scheduler: Scheduler
    ): Eff[U, A] = {

      val tranState = for {
        s <- get[U, Transaction] // 実行前にUとする
      } yield s

      tranState.map {
        case ReadTransaction =>
          val session = ScalikejdbcDbSession(DB.readOnlySession())
          runTransaction(effects, session)
        case _ =>
          DB.localTx { s =>
            val session = ScalikejdbcDbSession(s)
            runTransaction(effects, session)
          }
      }.flatten

    }
  }

}
