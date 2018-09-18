package example.shared.lib.test.eff.db.slick

import com.github.tototoshi.slick.GenericJodaSupport

import slick.dbio.{
  AndThenAction,
  AsTryAction,
  CleanUpAction,
  DatabaseAction,
  FailedAction,
  FailureAction,
  FlatMapAction,
  FutureAction,
  NamedAction,
  SequenceAction,
  SuccessAction
}

import _root_.slick.jdbc.H2Profile

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReferenceArray

import scala.concurrent.{ ExecutionContext, Future, Promise }
import scala.util.{ Failure, Success }

import jp.eigosapuri.es.shared.lib.eff.db.slick.DBComponent

class TestDBComponent extends DBComponent {
  override protected val profile = H2Profile // DBは叩かないが、Database型定義のため必須

  import profile.api._
  override protected val db: Database = db

  override val jodaSupport: GenericJodaSupport = com.github.tototoshi.slick.MySQLJodaSupport

  val sequentialEc: ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(1))
  override def run[R](a: DBIO[R]): Future[R]            = runInternal(a)(sequentialEc)
  override def runTransaction[R](a: DBIO[R]): Future[R] = runInternal(a)(sequentialEc)

  // だいぶ強引だけどまあこれでとりあえず動くぞ！
  private def runInternal[R](a: DBIOAction[R, NoStream, Nothing])(implicit ec: ExecutionContext): Future[R] = {
    a match {
      case SuccessAction(v) => Future.successful(v)
      case FailureAction(t) => Future.failed(t)
      case FutureAction(f)  => f
      case FlatMapAction(base, f, _) =>
        runInternal(base).flatMap(x => runInternal(f(x))) // IntelliJは赤いけど通る
      case AndThenAction(actions) =>
        actions.tail
          .foldLeft(runInternal(actions.head)) { (x, y) =>
            x.flatMap(v => runInternal(y))
          }
          .asInstanceOf[Future[R]]
      case sa @ SequenceAction(actions) =>
        val len     = actions.length
        val results = new AtomicReferenceArray[Any](len)

        def runIndex(pos: Int): Future[Any] = {
          if (pos == len) Future.successful {
            val b = sa.cbf()
            var i = 0
            while (i < len) {
              b += results.get(i)
              i += 1
            }
            b.result()
          } else
            runInternal(actions(pos))(sequentialEc).flatMap { (v: Any) =>
              results.set(pos, v)
              runIndex(pos + 1)
            }
        }

        runIndex(0).asInstanceOf[Future[R]]
      case CleanUpAction(base, f, keepFailure, _) =>
        val fu = runInternal(base)
        fu.onComplete {
          case Success(_) => runInternal(f(None))
          case Failure(e) => runInternal(f(Some(e)))
        }
        fu
      case FailedAction(a) =>
        runInternal(a).failed.asInstanceOf[Future[R]]
      case AsTryAction(a) =>
        val p = Promise[R]()
        runInternal(a).onComplete(v => p.success(v.asInstanceOf[R]))
        p.future
      case NamedAction(a, _) =>
        runInternal(a)
      case a: DatabaseAction[_, _, _] =>
        throw new SlickException(s"Unsupported database action $a for $this")
    }
  }

}
