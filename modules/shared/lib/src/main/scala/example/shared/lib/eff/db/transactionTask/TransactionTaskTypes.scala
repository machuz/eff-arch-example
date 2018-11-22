package example.shared.lib.eff.db.transactionTask

import org.atnos.eff.{ <=, |=, Fx }

import cats.data.Reader
import example.shared.lib.eff.ErrorEither
import example.shared.lib.transactionTask.{
  ReadTransaction,
  ReadWriteTransaction,
  Transaction,
  TransactionTask,
  TransactionTask2
}
import monix.eval.Task

object TransactionTaskType {
  type Resource <: Transaction
}

trait TransactionTaskTypes {
  type TranTask[A]  = TransactionTask[Transaction, A]
  type _trantask[R] = TranTask |= R
//  type _trantask[R] = TranTask |= R
  type _TranTask[R] = TranTask <= R

  type TranTask2[A]  = TransactionTask2[A]
  type _trantask2[R] = TranTask2 |= R
  type _TranTask2[R] = TranTask2 <= R

//
//  type RTranTask[A]  = TransactionTask[ReadTransaction, A]
//  type _rtrantask[R] = RTranTask |= R
//  type _RTranTask[R] = RTranTask <= R
//
//  type WTranTask[A]  = TransactionTask[ReadWriteTransaction, A]
//  type _wtrantask[R] = WTranTask |= R
//  type _WTranTask[R] = WTranTask <= R

  type ReaderDbSession[A]  = Reader[Transaction, A]
  type _readerDbSession[R] = ReaderDbSession |= R

  type DBStack = Fx.fx4[TranTask2, ReaderDbSession, Task, ErrorEither]
}
