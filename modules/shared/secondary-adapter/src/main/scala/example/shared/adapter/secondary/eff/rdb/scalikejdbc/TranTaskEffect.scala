package example.shared.adapter.secondary.eff.rdb.scalikejdbc

import example.shared.lib.eff.myEff._
import example.shared.lib.eff.atnosEffSyntax._
import monix.eval.Task
import org.atnos.eff.{ /=, Eff, Fx, Member }

import example.shared.lib.eff.ErrorEither
import example.shared.lib.eff.db.transactionTask.TransactionTaskInterpreter
import monix.execution.Scheduler
import scalikejdbc.DBSession

trait TranTaskEffect {

  implicit class TranTaskReadOps[R, A](effects: Eff[R, A]) {
    def runReadTranTask[U](
      implicit m2: Member.Aux[ErrorEither, U, Fx.fx1[Task]],
      m1: Member.Aux[TranTask, R, U],
      et: Task /= U,
      int: TransactionTaskInterpreter,
      s: Scheduler
    ): Eff[U, A] = int.run(effects)
  }

  implicit class TranTaskWriteOps[R, A](effects: Eff[R, A]) {
    def runReadWriteTranTask[U](
      implicit m2: Member.Aux[ErrorEither, U, Fx.fx1[Task]],
      m1: Member.Aux[TranTask, R, U],
      et: Task /= U,
      int: TransactionTaskInterpreter,
      s: Scheduler
    ): Eff[U, A] = int.runWithTransaction(effects)
  }
}

object TranTaskEffect {

  def withDBSession[R: _trantask, A](f: DBSession => A): Eff[R, A] =
    ScalikejdbcDbSession.sessionAsk.map(f).send
}
