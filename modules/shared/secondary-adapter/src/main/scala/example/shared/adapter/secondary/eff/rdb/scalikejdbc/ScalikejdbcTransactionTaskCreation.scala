package example.shared.adapter.secondary.eff.rdb.scalikejdbc

import org.atnos.eff.Eff

import example.shared.lib.eff._
import example.shared.lib.transactionTask.TransactionTask

trait ScalikejdbcTransactionTaskCreation {

  final def fromTranTask[R: _trantask, A](
    t: TransactionTask[A]
  ): Eff[R, A] = {
    Eff.send[TranTask, R, A](t)
  }

  final def pureTranTask[R: _trantask, A](
    a: A
  ): Eff[R, A] = {
    Eff.send[TranTask, R, A](TransactionTask(a))
  }

//  final def write[R: _trantask, A](
//    t: A
//  ): Eff[R, A] = {
//    Eff.send[TranTask, R, A](Write(t))
//  }

}
