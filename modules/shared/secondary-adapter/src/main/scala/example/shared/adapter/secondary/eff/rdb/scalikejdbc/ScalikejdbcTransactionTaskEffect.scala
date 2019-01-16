package example.shared.adapter.secondary.eff.rdb.scalikejdbc
import org.atnos.eff.{ Eff, Member }

import cats.data._
import cats.implicits._
import example.shared.adapter.secondary.transactionTask.scalikejdbc.ScalikejdbcDbSession
import example.shared.lib.transactionTask.DbSession
import example.shared.lib.transactionTask.Transaction.{ ReadTransaction, ReadWriteTransaction }
import scalikejdbc.DB
//import org.atnos.eff.all._
//import org.atnos.eff.syntax.all._

import example.shared.lib.eff._
import example.shared.lib.eff.atnosEff._
import example.shared.lib.eff.atnosEffSyntax._
import example.shared.lib.eff.atnosEffCreation._
import example.shared.lib.transactionTask.Transaction.Initialize
import example.shared.lib.transactionTask.{ Transaction, TransactionTask }

trait ScalikejdbcTransactionTaskEffect {

  implicit class TranTaskOps[R, A](effects: Eff[R, A]) {
    def runTranTask[U](
      member1: Member.Aux[TransactionTask, R, U],
      member2: Member[ReaderDbSession, U],
      member3: Member.Aux[StateTransaction, R, U],
//      m3: _stateTransaction[U]
    ): Eff[U, A] = {

      effects.runStateU[Transaction, U](Initialize)(member3).map {
        case (tran, a) =>
          tran match {
            case ReadTransaction =>
              val session = DB.readOnlySession()
              val i       = a.pureEff[U]

            case ReadWriteTransaction =>
              a
            case _ =>
          }
      }

      for {
        (a, tran) <- effects.runStateU[Transaction, U](Initialize)(member3)
        _ <- {
          tran match {
            case ReadTransaction =>
              val session = ScalikejdbcDbSession(DB.readOnlySession())
              val i       = a.pureEff[U].runReader[DbSession](session)(member2.aux)
              i
            case _ =>
              val i = DB.localTx { s =>
                val session = ScalikejdbcDbSession(s)
                a.pureEff[U].runReader[DbSession](session)(member2.aux)
              }
              i
          }
        }
      } yield ()

    }
  }

}
