package example.shared.lib.eff.db.transactionTask

import org.atnos.eff.Eff

trait TransactionTaskCreation extends TransactionTaskTypes {

  final def fromTranTask[R: _trantask, A](transactionTask: TranTask[A]): Eff[R, A] = {
    Eff.send[TranTask, R, A](transactionTask)
  }

//  def successful[R: _dbio, A](a: A): Eff[R, A] = {
//    send[DBIO, R, A](DBIO.successful(a))
//  }
//
//  def failed[R: _dbio, A](t: Throwable): Eff[R, A] = {
//    send[DBIO, R, A](DBIO.failed(t))
//  }

}
