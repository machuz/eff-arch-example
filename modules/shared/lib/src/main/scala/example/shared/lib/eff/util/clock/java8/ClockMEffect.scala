package example.shared.lib.eff.util.clock.java8

import org.atnos.eff.syntax.all._
import org.atnos.eff.{ |=, Eff, Member, MemberIn }

import cats.data.Reader

import java.time.Clock
import example.shared.lib.eff.util.clock.java8.interpreter.ClockMInterpreter
import example.shared.lib.eff.util.clock.java8.ClockMInterpretationTypes.ReaderClock

object ClockMInterpretationTypes {
  type ReaderClock[A]  = Reader[Clock, A]
  type _readerClock[R] = ReaderClock |= R
}

trait ClockMOps {
  implicit class ClockMOps[R, A](effects: Eff[R, A]) {
    def runClock[U](clock: Clock)(
      implicit interpreter: ClockMInterpreter,
      m1: Member.Aux[ClockM, R, U],
      m2: Member[ReaderClock, U]
    ) = {
      interpreter.run(effects).runReader(clock)
    }
  }
}

trait ClockMEffect extends ClockMOps with ClockMTypes
