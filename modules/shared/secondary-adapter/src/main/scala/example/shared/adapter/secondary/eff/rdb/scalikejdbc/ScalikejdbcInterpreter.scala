package example.shared.adapter.secondary.eff.rdb.scalikejdbc

import org.atnos.eff.{ Eff, Member, Translate }
import org.atnos.eff.interpret.translate

import example.shared.adapter.secondary.transactionTask.scalikejdbc._
import example.shared.lib.eff._
import example.shared.lib.eff.atnosEff._
import example.shared.lib.transactionTask.{ Transaction, TransactionTask }

trait ScalikejdbcInterpreter {

  type TranTask[X] = TransactionTask[Transaction, X]

  def runTransaction[R, U, A, B](
    effect: Eff[R, A]
  )(
    implicit m: Member.Aux[TranTask, R, U],
    m1: _task[U],
    m2: _errorEither[U]
  ): Eff[U, A] = {

    translate(effect)(new Translate[TranTask, U] {
      def apply[X](ax: TranTask[X]): Eff[U, X] = {
        val x = sessionAsk
          .map { implicit session =>
            ax.run()
          }
          .run
          .flatten
        fromTask(x)
      }
    })
  }

}
