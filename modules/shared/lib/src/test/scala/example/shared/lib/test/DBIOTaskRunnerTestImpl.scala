package example.shared.lib.test

import example.shared.lib.test.eff.db.slick.TestDBComponent

import java.util.concurrent.Executors

import jp.eigosapuri.es.shared.lib.dddSupport.adapter.secondary.repository.DBIOTaskRunner
import slick.dbio._

import scala.concurrent.{ ExecutionContext, Future }

class DBIOTaskRunnerTestImpl extends DBIOTaskRunner[DBIO] {

  val db = new TestDBComponent

  val sequentialEc: ExecutionContext =
    ExecutionContext.fromExecutor(Executors.newFixedThreadPool(1))

  override def run[R](a: DBIO[R])(implicit ctx: ExecutionContext): Future[R] = db.run(a)

  override def runTransaction[R](a: DBIO[R])(implicit ctx: ExecutionContext): Future[R] = db.runTransaction(a)
}
