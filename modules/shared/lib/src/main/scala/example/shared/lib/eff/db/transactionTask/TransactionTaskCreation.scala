package example.shared.lib.eff.db.transactionTask

import org.atnos.eff.{ |=, Eff }

import example.shared.lib.transactionTask.{ ReadTransaction, Transaction, TransactionTask }

trait TransactionTaskCreation extends TransactionTaskTypes {

  final def fromTranTask[R: _trantask, A](
    t: TranTask[A]
  ): Eff[R, A] = {
    Eff.send[TranTask, R, A](t)
  }

  final def fromTranTask2[R: _trantask2, A](
    t: TranTask2[A]
  ): Eff[R, A] = {
    Eff.send[TranTask2, R, A](t)
  }

//
//  final def fromReadTranTask[R: _trantask, A, Re >: ReadTransaction](t: TransactionTask[Re, A]): Eff[R, A] = {
//    Eff.send[TranTask, R, A](t)
//  }
//
//  final def fromWriteTranTask[R: _wtrantask, A](t: WTranTask[A]): Eff[R, A] = {
//    Eff.send[WTranTask, R, A](t)
//  }

}
