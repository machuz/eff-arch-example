package example.shared.adapter.secondary.eff.rdb.scalikejdbc

import org.atnos.eff.Eff

import example.shared.lib.eff._
import example.shared.lib.transactionTask.TransactionTask

trait ScalikejdbcTransactionTaskCreation {

  final def fromTranTask[R: _trantask, A](
    t: A
  ): Eff[R, A] = {
    Eff.send[TranTask, R, A](TransactionTask(t))
  }

//  final def write[R: _trantask, A](
//    t: A
//  ): Eff[R, A] = {
//    Eff.send[TranTask, R, A](Write(t))
//  }

}
