package example.shared.lib.transactionTask

import org.atnos.eff.Eff

import example.shared.lib.eff._stateTransaction
import example.shared.lib.eff.atnosEff._

sealed class Transaction

object Transaction {
  case object ReadTransaction      extends Transaction
  case object ReadWriteTransaction extends Transaction

  def read[R: _stateTransaction]: Eff[R, Unit] =
    for {
      nowTransaction <- get[R, Transaction]
      res <- {
        nowTransaction match {
          case ReadTransaction      => put[R, Transaction](ReadTransaction)
          case ReadWriteTransaction => put[R, Transaction](ReadWriteTransaction)
        }
      }
    } yield res

  def readWrite[R: _stateTransaction]: Eff[R, Unit] =
    for {
      res <- put[R, Transaction](ReadWriteTransaction)
    } yield res

}
