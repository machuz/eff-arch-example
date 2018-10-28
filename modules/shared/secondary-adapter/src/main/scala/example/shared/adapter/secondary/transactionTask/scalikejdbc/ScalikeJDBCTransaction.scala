package example.shared.adapter.secondary.transactionTask.scalikejdbc

import example.shared.lib.transactionTask._
import scalikejdbc.DBSession

abstract class ScalikeJdbcTransaction(val session: DBSession)

class ScalikeJdbcReadTransaction(session: DBSession) extends ScalikeJdbcTransaction(session) with ReadTransaction

class ScalikeJdbcReadWriteTransaction(session: DBSession)
  extends ScalikeJdbcTransaction(session)
  with ReadWriteTransaction
