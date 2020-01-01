package example.shared.lib.eff.db.transactionTask

import org.atnos.eff.{ <=, |=, Fx }

import example.shared.lib.eff.ErrorEither
import monix.eval.Task

trait TransactionTaskTypes {
  // TranTask
  type TranTask[A]  = TransactionTask[A]
  type _trantask[R] = TranTask |= R
  type _TranTask[R] = TranTask <= R

  type TranTaskStack = Fx.fx3[TransactionTask, ErrorEither, Task]
}
