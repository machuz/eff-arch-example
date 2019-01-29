package example.shared.adapter.secondary.transactionTask.scalikejdbc

import example.shared.lib.transactionTask.DbSession
import scalikejdbc.DBSession

case class ScalikejdbcDbSession(value: DBSession) extends DbSession
