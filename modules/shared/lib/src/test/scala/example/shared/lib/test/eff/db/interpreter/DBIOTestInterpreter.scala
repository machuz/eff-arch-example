package example.shared.lib.test.eff.db.interpreter

import com.google.inject.Inject

import org.atnos.eff._
import org.atnos.eff.addon.scalaz.either._
import org.atnos.eff.addon.monix.task._
import org.atnos.eff.all.ask
import org.atnos.eff.interpret._

import monix.eval.Task
import slick.SlickException
import slick.dbio.{
  AndThenAction,
  AsTryAction,
  CleanUpAction,
  DBIO,
  DBIOAction,
  DatabaseAction,
  FailedAction,
  FailureAction,
  FlatMapAction,
  FutureAction,
  NamedAction,
  NoStream,
  SequenceAction,
  SuccessAction
}

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReferenceArray

import scalaz.Scalaz.ToEitherOps
import scala.util.{ Failure => F, Success => S }
import scala.concurrent.{ ExecutionContext, Future, Promise }

import jp.eigosapuri.es.shared.lib.dddSupport.{ ErrorCode, EsError }
import jp.eigosapuri.es.shared.lib.eff._errorEither
import jp.eigosapuri.es.shared.lib.eff.db.slick.DBComponent

class DBIOTestInterpreter @Inject()(
  dbc: DBComponent
) {

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
            Task.deferFuture(dbc.run(ax)).materialize.map {
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
