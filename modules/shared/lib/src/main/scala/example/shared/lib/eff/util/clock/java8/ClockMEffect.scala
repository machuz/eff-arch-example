package example.shared.lib.eff.util.clock.java8

import org.atnos.eff.syntax.all._
import org.atnos.eff.{ |=, Eff, Member }

import cats.data.Reader

import java.time.Clock

import example.shared.lib.eff.util.clock.java8.interpreter.ClockMInterpreter
import example.shared.lib.eff.util.clock.java8.ClockMInterpretationTypes.ReaderClock

object ClockMInterpretationTypes {
  type ReaderClock[A]  = Reader[Clock, A]
  type _readerClock[R] = ReaderClock |= R
}

trait ClockMOps extends ClockMInterpreter {
  implicit class ClockMOps[R, A](effects: Eff[R, A]) {
    def runClock[U](
      implicit m1: Member.Aux[ClockM, R, U],
      m2: Member[ReaderClock, U]
    ): Eff[m2.Out, A] = {
      run(effects).runReader(Clock.systemUTC())(m2)
    }
  }
}

object ClockMEffect extends ClockMOps with ClockMCreation
