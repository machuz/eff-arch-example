package example.shared.lib.eff.db.transactionTask

import org.atnos.eff.{ /=, Eff, Fx, Member }

import example.shared.lib.eff.myEff._
import example.shared.lib.eff.ErrorEither
import monix.eval.Task
import monix.execution.Scheduler

abstract class TransactionTaskInterpreter {

  def run[R, U, A](
    effect: Eff[R, A]
  )(
    implicit m1: Member.Aux[TranTask, R, U],
    m2: Member.Aux[ErrorEither, U, Fx.fx1[Task]],
    et: Task /= U,
    s: Scheduler
  ): Eff[U, A]

  def runWithTransaction[R, U, A](
    effect: Eff[R, A]
  )(
    implicit m1: Member.Aux[TranTask, R, U],
    m2: Member.Aux[ErrorEither, U, Fx.fx1[Task]],
    et: Task /= U,
    s: Scheduler
  ): Eff[U, A]
}
