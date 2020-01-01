package example.shared.adapter.secondary.eff.rdb.scalikejdbc

import example.shared.lib.eff.db.transactionTask.{ DbSession, TransactionTask }
import monix.eval.Task
import scalikejdbc.DBSession

case class ScalikejdbcDbSession(value: DBSession) extends DbSession

object ScalikejdbcDbSession {

  def applyUpcast(value: DBSession): DbSession = {
    ScalikejdbcDbSession(value)
  }

  def downCast(v: DbSession): ScalikejdbcDbSession = v.asInstanceOf[ScalikejdbcDbSession]

  def sessionAsk: TransactionTask[DBSession] =
    new TransactionTask[DBSession] {
      def execute(resource: DbSession): Task[DBSession] = {
        Task.delay(downCast(resource).value)
      }
    }
}
