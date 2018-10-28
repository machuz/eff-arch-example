package example.shared.adapter.secondary.transactionTask.scalikejdbc

import example.shared.lib.transactionTask._
import scalikejdbc.DBSession

abstract class ScalikejdbcTransaction(val session: DBSession)

class ScalikejdbcReadTransaction(session: DBSession) extends ScalikejdbcTransaction(session) with ReadTransaction

class ScalikejdbcReadWriteTransaction(session: DBSession)
  extends ScalikejdbcTransaction(session)
  with ReadWriteTransaction
