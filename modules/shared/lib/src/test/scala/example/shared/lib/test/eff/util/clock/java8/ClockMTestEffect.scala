package example.shared.lib.test.eff.util.clock.java8

import org.atnos.eff.syntax.reader._
import org.atnos.eff.{ Eff, Member }

import java.time.Clock
import example.shared.lib.eff.util.clock.java8.ClockM
import example.shared.lib.eff.util.clock.java8.ClockMInterpretationTypes.{ _readerClock, ReaderClock }
import example.shared.lib.eff.util.clock.java8.interpreter.ClockMInterpreter

trait ClockMTestOps {
  implicit class ClockMTestOps[R, A](effects: Eff[R, A]) {
    def testRunClock[U](c: Clock)(
      implicit interpreter: ClockMInterpreter,
      m1: Member.Aux[ClockM, R, U],
      m2: Member[ReaderClock, U]
    ): Eff[m2.Out, A] = interpreter.run(effects).runReader(c)(m2)
  }
}

object ClockMTestEffect extends ClockMTestOps
