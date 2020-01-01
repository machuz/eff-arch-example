package example.shared.lib.eff.db.transactionTask

import org.atnos.eff.Eff
import example.shared.lib.eff.myEff._

trait TransactionTaskCreation {

  final def fromTranTask[R: _trantask, A](transactionTask: TranTask[A]): Eff[R, A] = {
    Eff.send[TranTask, R, A](transactionTask)
  }

}
