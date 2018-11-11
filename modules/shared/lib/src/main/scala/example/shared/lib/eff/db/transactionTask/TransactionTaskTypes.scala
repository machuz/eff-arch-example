package example.shared.lib.eff.db.transactionTask

import org.atnos.eff.{ <=, |=, Fx }

import example.shared.lib.eff.ErrorEither
import example.shared.lib.transactionTask.{ Transaction, TransactionTask }
import monix.eval.Task

trait TransactionTaskTypes {
  type TranTask[A]  = TransactionTask[Transaction, A]
  type _trantask[R] = TranTask |= R
  type _TranTask[R] = TranTask <= R
  type DBStack      = Fx.fx3[TranTask, Task, ErrorEither]
}
