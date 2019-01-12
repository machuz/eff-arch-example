package example.shared.adapter.secondary.eff.rdb.scalikejdbc
import org.atnos.eff.{ Eff, Member }

import example.shared.lib.eff._
import example.shared.lib.eff.atnosEff._
import example.shared.lib.eff.atnosEffSyntax._
import example.shared.lib.eff.atnosEffCreation._
import example.shared.lib.transactionTask.TransactionTask

trait ScalikejdbcTransactionTaskEffect {

  implicit class TranTaskOps[R, A](effects: Eff[R, A]) {
    def runTranTask[U](
      member1: Member.Aux[TransactionTask, R, U],
      member2: Member[ReaderDbSession, U],
      member3: Member[StateTransaction, U],
    ): Eff[U, A] = {

      effects.runState()


    }
  }

}
