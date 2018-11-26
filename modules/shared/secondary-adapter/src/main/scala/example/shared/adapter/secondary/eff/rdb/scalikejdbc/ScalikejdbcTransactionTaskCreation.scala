package example.shared.adapter.secondary.eff.rdb.scalikejdbc

import org.atnos.eff.Eff

import example.shared.adapter.secondary.transactionTask.scalikejdbc.{ Read, Write }
import example.shared.lib.eff._

trait ScalikejdbcTransactionTaskCreation {

  final def read[R: _trantask, A](
    t: A
  ): Eff[R, A] = {
    Eff.send[TranTask, R, A](Read(t))
  }

  final def write[R: _trantask, A](
    t: A
  ): Eff[R, A] = {
    Eff.send[TranTask, R, A](Write(t))
  }

}
