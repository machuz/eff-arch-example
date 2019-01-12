package example.shared.lib.eff.db.transactionTask

import org.atnos.eff.{ /=, <=, |=, Fx }

import cats.data.{ Reader, State }
import example.shared.lib.eff.ErrorEither
import example.shared.lib.transactionTask.{ DbSession, Transaction, TransactionTask }
import monix.eval.Task

trait TransactionTaskTypes {

  type TranTask[A]  = TransactionTask[A]
  type _trantask[R] = TranTask |= R
  type _TranTask[R] = TranTask <= R

  type StateTransaction[A]  = State[Transaction, A]
  type _stateTransaction[R] = StateTransaction |= R

  type ReaderDbSession[A]  = Reader[DbSession, A]
  type _readerDbSession[R] = ReaderDbSession |= R

  type DBStack = Fx.fx4[TranTask, ReaderDbSession, Task, ErrorEither]
}
