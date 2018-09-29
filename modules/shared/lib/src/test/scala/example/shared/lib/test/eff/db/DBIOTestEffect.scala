package example.shared.lib.test.eff.db

import org.atnos.eff.syntax.all._
import org.atnos.eff.addon.monix.task._task
import org.atnos.eff.{ Eff, Fx, Member }

import _root_.slick.dbio.DBIO
import example.shared.lib.test.eff.db.interpreter.DBIOTestInterpreter
import example.shared.lib.test.eff.db.slick.TestDBComponent

import scala.concurrent.ExecutionContext

import example.shared.lib.eff._errorEither
import example.shared.lib.eff.db.DBIOCreation
import example.shared.lib.eff.db.DBIOTypes.{ _readerDB, ReaderDB }
import example.shared.lib.eff.db.slick.DBComponent
import example.shared.lib.test.eff.db.slick.TestDBComponent

trait DBIOTestOps {
  implicit class DBIOTestOps[R, A](effects: Eff[R, A]) {

    def testRunDBIO[U](
      implicit m1: Member.Aux[DBIO, R, U],
      m3: _task[U],
      m4: _errorEither[U],
      ec: ExecutionContext
    ) = {
      val dbc         = new TestDBComponent
      val interpreter = new DBIOTestInterpreter(dbc)
      interpreter.run(effects)
    }
  }
}

object DBIOTestEffect extends DBIOTestOps with DBIOCreation {}
