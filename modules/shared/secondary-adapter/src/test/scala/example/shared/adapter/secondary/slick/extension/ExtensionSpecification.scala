package jp.eigosapuri.es.shared.adapter.secondary.slick.extension

import slick.dbio._

import scala.concurrent.{ ExecutionContext, Future }

import jp.eigosapuri.es.shared.lib.dddSupport.adapter.secondary.repository.DBIOTaskRunner
import jp.eigosapuri.es.shared.lib.test.AbstractSpecification

class ExtensionSpecification extends AbstractSpecification with DBIOTaskRunner[DBIO] {

  override def run[R](a: DBIO[R])(implicit ctx: ExecutionContext): Future[R] = a match {
    case SuccessAction(v) => Future.successful(v)
    case FailureAction(t) => Future.failed(t)
    case FlatMapAction(base, f, ec) =>
      run(base).flatMap { v =>
        run(f(v))
      }
    case SequenceAction(actions) =>
      Future
        .sequence {
          actions.map { action =>
            run(action)
          }
        }
        .asInstanceOf[Future[R]]
    case FutureAction(f) => f
    case action if action.nonFusedEquivalentAction != action =>
      run(action.nonFusedEquivalentAction)
    case _ => throw new RuntimeException("no support") // testでは利用しない
    //    case AndThenAction(actions) => sys.error("not impl")
    //    case CleanUpAction(base, f, keepFailure, ec) => sys.error("not impl")
    //    case FailedAction(_) => sys.error("not impl")
    //    case AsTryAction(_) => sys.error("not impl")
    //    case NamedAction(_, _) => sys.error("not impl")
    //    case a: SynchronousDatabaseAction[_, _, _, _] => sys.error("not impl")
    //    case a: DatabaseAction[_, _, _] => sys.error("not impl")
  }

  override def runTransaction[R](a: DBIO[R])(implicit ctx: ExecutionContext): Future[R] = run(a)
}
