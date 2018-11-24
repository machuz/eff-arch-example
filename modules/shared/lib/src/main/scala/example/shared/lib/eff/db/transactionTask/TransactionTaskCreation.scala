package example.shared.lib.eff.db.transactionTask

import org.atnos.eff.{ |=, Eff }

import example.shared.lib.transactionTask.ReadTransaction

//import example.shared.lib.eff.db.transactionTask.TransactionTaskType.Resource
import example.shared.lib.transactionTask.{ Transaction, TransactionTask }

trait TransactionTaskCreation extends TransactionTaskTypes {

  final def fromTranTask[R: _trantask, A](
    t: TranTask[A]
  ): Eff[R, A] = {
    Eff.send[TranTask, R, A](t)
  }
//
//  final def fromTranTask2[R: _trantask, A](
//    t: TransactionTask[ReadTransaction, A]
//  ): Eff[R, A] = {
//    Eff.send[TransactionTask[ReadTransaction, _], R, A](t)
//  }

//
//  final def fromReadTranTask[R: _trantask, A, Re >: ReadTransaction](t: TransactionTask[Re, A]): Eff[R, A] = {
//    Eff.send[TranTask, R, A](t)
//  }
//
//  final def fromWriteTranTask[R: _wtrantask, A](t: WTranTask[A]): Eff[R, A] = {
//    Eff.send[WTranTask, R, A](t)
//  }

}
