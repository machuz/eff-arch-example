package example.shared.adapter.secondary.transactionTask.scalikejdbc

import example.shared.lib.transactionTask._
import scalikejdbc.DBSession

abstract class ScalikeJDBCTransaction(val session: DBSession)

class ScalikeJDBCReadTransaction(session: DBSession) extends ScalikeJDBCTransaction(session) with ReadTransaction

class ScalikeJDBCReadWriteTransaction(session: DBSession)
  extends ScalikeJDBCTransaction(session)
  with ReadWriteTransaction
