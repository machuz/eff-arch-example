package example.shared.adapter.secondary.eff.db.scalikejdbc

import org.atnos.eff.{ Eff, Member, Translate }
import org.atnos.eff.interpret.translate

import example.shared.lib.eff._
import example.shared.lib.transactionTask.TransactionTask

import scala.concurrent.ExecutionContext

trait ScalikejdbcInterpreter {

  def runTransaction[R, U, A, B](
    effect: Eff[R, A]
  )(
    implicit m: Member.Aux[TransactionTask, R, U],
    m1: _task[U],
    m2: _errorEither[U],
    m3: _readerTran[U],
    ec: ExecutionContext
  ): Eff[U, A] = {

    translate(effect)(new Translate[TransactionTask, U] {
      def apply[X](ax: TransactionTask[X]): Eff[U, X] = {
        for {
          resE <- fromTask {
            Task.fromFuture(db.runTransaction(ax)).materialize.map {
              case S(r) =>
                r.right
              case F(e) =>
                EsError.DatabaseError(e, ErrorCode.SERVER_ERROR).left
            }
          }
          res <- fromDisjunction[U, EsError, X](resE)
        } yield res
      }
    })
  }

  def run[R, U, A, B](
    effect: Eff[R, A]
  )(
    implicit m: Member.Aux[DBIO, R, U],
    m1: _task[U],
    m2: _errorEither[U],
    ec: ExecutionContext
  ): Eff[U, A] = {

    translate(effect)(new Translate[DBIO, U] {
      def apply[X](ax: DBIO[X]): Eff[U, X] = {
        for {
          resE <- fromTask {
            Task.fromFuture(db.run(ax)).materialize.map {
              case S(r) =>
                r.right
              case F(e) =>
                EsError.DatabaseError(e, ErrorCode.SERVER_ERROR).left
            }
          }
          res <- fromDisjunction[U, EsError, X](resE)
        } yield res
      }
    })
  }
}
