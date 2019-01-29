package example.shared.adapter.secondary.transactionTask.scalikejdbc
import example.shared.lib.transactionTask.{ Transaction, TransactionTask }

sealed abstract class ScalikejdbcTransactionTask[+A]
case class Read[+A](v: A)
case class Write[+A](v: A)
