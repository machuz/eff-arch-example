package example.shared.adapter.secondary.transactionTask.scalikejdbc
import example.shared.lib.transactionTask.TransactionTask

sealed abstract class ScalikejdbcTransactionTask[+A] extends TransactionTask[A]
case class Read[+A](v: A)                            extends ScalikejdbcTransactionTask[A]
case class Write[+A](v: A)                           extends ScalikejdbcTransactionTask[A]
