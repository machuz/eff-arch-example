package example.shared.adapter.secondary.eff.rdb.scalikejdbc

import org.atnos.eff.{ Eff, Fx, IntoPoly, Member, ReaderEffect, Translate }
import org.atnos.eff.syntax._
import org.atnos.eff.interpret.translate

import cats.data.{ Kleisli, Reader }
import cats.~>
import example.shared.adapter.secondary.eff._
import example.shared.lib.eff._
import example.shared.lib.eff.atnosEff._
import example.shared.lib.eff.atnosEffCreation._
import example.shared.lib.transactionTask.Transaction._
import example.shared.lib.eff.util.clock.java8.ClockMInterpretationTypes.ReaderClock
import example.shared.lib.transactionTask.DbSession
import monix.eval.Task
import scalikejdbc.DB

trait ScalikejdbcInterpreter {

  def runTransaction[R, U, A, B](
    effect: Eff[R, A]
  )(
    implicit t1: Member.Aux[TranTask, R, U],
    r1: Member[ReaderDbSession, U],
    m1: _task[U],
    m2: _errorEither[U],
    m3: _eval[U],
    m4: _readerDbSession[U],
  ): Eff[U, A] = {

    translate(effect)(new Translate[TranTask, U] {
      def apply[X](ax: TranTask[X]): Eff[U, X] = {

        for {
          s <- ask[U, DbSession]
          _ <- {
            DB.futureLocalTx(session => task.execute(new ScalikeJDBCReadWriteTransaction(session)))
          }
        } yield s


        DB.futureLocalTx { session =>

          task.execute(new ScalikeJDBCReadWriteTransaction(session)))
        }
        ax.

        for {
          _ <- ask
        } yield ()


        val transaction = ax match {
          case x: Read[X] =>
            val session = DB.readOnlySession()
            println("read")
            val t = new ScalikejdbcReadTransaction(session)
            val r = fromTask(Task.now(x.v))
            val res = ReaderEffect.runReader(t.asInstanceOf[Transaction])(r)(r1.aux)
//              .transform()
            val hog: Eff[U, X] = res

          case x: Write[X] =>
            println("write")
            val t = DB.localTx(session => new ScalikejdbcReadWriteTransaction(session))
            val r = fromTask(Task.now(x.v))
            ReaderEffect.runReader(t.asInstanceOf[Transaction])(r)(r1.aux).into[U]
        }
        transaction
      }
    })
  }

//  private def checkTransaction[T <:]()

}
