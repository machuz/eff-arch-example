package example.shared.adapter.secondary.eff.rdb.scalikejdbc

import com.typesafe.scalalogging.LazyLogging

import org.atnos.eff.{ /=, Continuation, Eff, Fx, Interpret, Interpreter, Member }

import cats.Traverse
import example.shared.adapter.secondary.eff.rdb.scalikejdbc.ScalikejdbcInterpreter.ScalikejdbcInterpreterInternal
import example.shared.adapter.secondary.rdb.scalikejdbc.DbComponent
import example.shared.lib.eff.myEff._
import example.shared.lib.eff.atnosEff._
import example.shared.lib.eff.atnosEffSyntax._
import example.shared.lib.dddSupport.Error
import example.shared.lib.dddSupport.Error.DatabaseError
import example.shared.lib.eff.ErrorEither
import example.shared.lib.eff.db.transactionTask.{
  TransactionTask,
  TransactionTaskInstances,
  TransactionTaskInterpreter
}
import javax.inject.Inject
import monix.eval.Task
import monix.execution.Scheduler
import scalikejdbc.{ ConnectionPool, DB, DBSession }

class ScalikejdbcInterpreter @Inject()(
  dbc: DbComponent
) extends TransactionTaskInterpreter
  with LazyLogging {

  private val internalInterpreter = new ScalikejdbcInterpreterInternal {

    override protected def readRunner[U, A](
      tran: TranTask[A]
    )(
      implicit ev: ErrorEither /= U,
      et: Task /= U,
      s: Scheduler
    ): Eff[U, A] = {
      lazy val session = dbc.getDB.readOnlySession()

      val res =
        fromTask[U, Error Either A](
          Task.fromFuture(
            tran
              .execute(ScalikejdbcDbSession.applyUpcast(session))
              .runToFuture(s)
              .map(Right(_))
              .recover {
                case e: Error     => Left(e)
                case e: Throwable => Left(Error.DatabaseError(e))
              }
          )
        ).flatMap(x => fromError(x))

      readErrorHandle[U, A](session, res)
    }

    override protected def readWriteRunner[U, A](
      tran: TranTask[A]
    )(
      implicit ev: ErrorEither /= U,
      et: Task /= U,
      s: Scheduler
    ): Eff[U, A] = {
      lazy val db = dbc.getDB
      db.begin()
      db.withinTx { session =>
        val res =
          fromTask[U, Error Either A](
            Task.fromFuture(
              tran
                .execute(ScalikejdbcDbSession.applyUpcast(session))
                .runToFuture(s)
                .map(Right(_))
                .recover {
                  case e: Error => Left(e)
                  case e        => Left(Error.DatabaseError(e))
                }
            )
          ).flatMap(x => fromError(x))

        readWriteErrorHandle[U, A](session, res)
      }
    }

    private def readErrorHandle[U, A](session: DBSession, effects: Eff[U, A])(
      implicit ev: ErrorEither /= U,
      et: Task /= U
    ): Eff[U, A] = {
      val tmp = org.atnos.eff.either
        .catchLeft[U, Error, A](effects) { e =>
          session.close()
          fromError[U, A](Left(e))
        }

      org.atnos.eff.addon.monix.task
        .taskAttempt(tmp)
        .flatMap {
          case Right(a) =>
            session.close()
            Eff.pure(a)
          case Left(e) =>
            session.close()
            fromError[U, A](Left(DatabaseError(e)))
        }
    }

    private def readWriteErrorHandle[U, A](session: DBSession, effects: Eff[U, A])(
      implicit ev: ErrorEither /= U,
      et: Task /= U
    ): Eff[U, A] = {
      val tmp = org.atnos.eff.either
        .catchLeft[U, Error, A](effects) { e =>
          session.connection.rollback()
          session.close()
          fromError[U, A](Left(e))
        }

      org.atnos.eff.addon.monix.task
        .taskAttempt(tmp)
        .flatMap {
          case Right(a) =>
            session.connection.commit()
            session.close()
            Eff.pure(a)
          case Left(e) =>
            session.connection.rollback()
            session.close()
            fromError[U, A](Left(DatabaseError(e)))
        }

    }

  }

  override def run[R, U, A](
    effect: Eff[R, A]
  )(
    implicit m1: Member.Aux[TranTask, R, U],
    m2: Member.Aux[ErrorEither, U, Fx.fx1[Task]],
    et: Task /= U,
    s: Scheduler
  ): Eff[U, A] = internalInterpreter.apply(effect, withTransaction = false)

  override def runWithTransaction[R, U, A](
    effect: Eff[R, A]
  )(
    implicit m1: Member.Aux[TranTask, R, U],
    m2: Member.Aux[ErrorEither, U, Fx.fx1[Task]],
    et: Task /= U,
    s: Scheduler
  ): Eff[U, A] = {
    internalInterpreter.apply(effect, withTransaction = true)
  }
}

object ScalikejdbcInterpreter {

  trait ScalikejdbcInterpreterInternal {
    protected def readRunner[U, A](
      tran: TranTask[A]
    )(
      implicit ev: ErrorEither /= U,
      et: Task /= U,
      s: Scheduler
    ): Eff[U, A]

    protected def readWriteRunner[U, A](
      tran: TranTask[A]
    )(
      implicit ev: ErrorEither /= U,
      et: Task /= U,
      s: Scheduler
    ): Eff[U, A]

    final def apply[R, U, A](
      effect: Eff[R, A],
      withTransaction: Boolean
    )(
      implicit m1: Member.Aux[TransactionTask, R, U],
      m2: Member.Aux[ErrorEither, U, Fx.fx1[Task]],
      et: Task /= U,
      s: Scheduler
    ): Eff[U, A] = {
      Interpret
        .runInterpreter(effect)(new Interpreter[TranTask, U, A, TransactionTask[A]] {
          override def onPure(a: A): Eff[U, TransactionTask[A]] = {
            Eff.pure(TransactionTask(a))
          }

          override def onEffect[X](x: TransactionTask[X], continuation: Continuation[U, X, TransactionTask[A]])
            : Eff[U, TransactionTask[A]] = {
            Eff.pure[U, TransactionTask[A]] {
              x.flatMap { y =>
                TransactionTask
                  .fromTask {
                    Task.fromFuture(continuation(y).runError.runAsync.runToFuture(s))
                  }
                  .flatMap(_.fold(TransactionTask.raiseError, x => x))
              }
            }
          }

          override def onLastEffect[X](x: TransactionTask[X], continuation: Continuation[U, X, Unit]): Eff[U, Unit] = {
            Eff.pure(
              x.map { x =>
                continuation(x)
              }
            )
          }

          override def onApplicativeEffect[X, T[_]](
            xs: T[TransactionTask[X]],
            continuation: Continuation[U, T[X], TransactionTask[A]]
          )(implicit T: Traverse[T]): Eff[U, TransactionTask[A]] = {
            Eff.pure[U, TransactionTask[A]] {
              T.traverse(xs)(x => x)(
                  TransactionTaskInstances.transactionTaskInstance
                )
                .flatMap { x =>
                  TransactionTask
                    .fromTask {
                      Task.fromFuture(
                        continuation(x).runError.runAsync.runToFuture(s)
                      )
                    }
                    .flatMap(_.fold(TransactionTask.raiseError, x => x))
                }
            }
          }

        })
        .flatMap { io: TransactionTask[A] =>
          if (withTransaction) readWriteRunner(io)
          else readRunner(io)
        }

    }

  }

}
