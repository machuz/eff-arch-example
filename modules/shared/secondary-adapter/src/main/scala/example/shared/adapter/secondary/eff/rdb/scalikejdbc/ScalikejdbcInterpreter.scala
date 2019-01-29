package example.shared.adapter.secondary.eff.rdb.scalikejdbc

import org.atnos.eff.{ Eff, Member, Translate }
import org.atnos.eff.interpret.translate

import example.shared.adapter.secondary.transactionTask.scalikejdbc._
import example.shared.lib.eff._
import example.shared.lib.eff.atnosEff._
import example.shared.lib.transactionTask.DbSession
import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.ExecutionContext

trait ScalikejdbcInterpreter {

  def runTransaction[R, U, A, B](
    effect: Eff[R, A],
    session: ScalikejdbcDbSession
  )(
    implicit ec: ExecutionContext,
    t1: Member.Aux[TranTask, R, U],
    m1: _task[U],
    scheduler: Scheduler
//    m2: _readerDbSession[U]
  ): Eff[U, A] = {

    translate(effect)(new Translate[TranTask, U] {
      def apply[X](ax: TranTask[X]): Eff[U, X] = {
        for {
          res <- {
            val task = ax.execute(session)
            task.runOnComplete(_ => session.value.close())
            fromTask(task)
          }
        } yield res
      }
    })
  }

}
