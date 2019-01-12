package example.shared.adapter.secondary.transactionTask.scalikejdbc

import example.shared.lib.transactionTask.DbSession
import scalikejdbc.DBSession

abstract class ScalikejdbcDbSession(val value: DBSession) extends DbSession
