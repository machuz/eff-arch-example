package example.shared.lib.test.eff.util.clock.java8

import org.atnos.eff.syntax.reader._
import org.atnos.eff.{ Eff, Member }

import java.time.Clock

import jp.eigosapuri.es.shared.lib.eff.util.clock.java8.ClockM
import jp.eigosapuri.es.shared.lib.eff.util.clock.java8.ClockMInterpretationTypes.ReaderClock
import jp.eigosapuri.es.shared.lib.eff.util.clock.java8.interpreter.ClockMInterpreter

trait ClockMTestOps extends ClockMInterpreter {
  implicit class ClockMTestOps[R, A](effects: Eff[R, A]) {
    def testRunClock[U](c: Clock)(
      implicit m1: Member.Aux[ClockM, R, U],
      m2: Member[ReaderClock, U]
    ): Eff[m2.Out, A] = run(effects).runReader(c)(m2)
  }
}

object ClockMTestEffect extends ClockMTestOps {}
