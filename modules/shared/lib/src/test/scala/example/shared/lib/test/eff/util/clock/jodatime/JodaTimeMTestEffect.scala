package example.shared.lib.test.eff.util.clock.jodatime

import org.atnos.eff.{ Eff, Member }

import example.shared.lib.eff.util.clock.joda.{ JodaTimeM, JodaTimeUtilsImpl }
import example.shared.lib.eff.util.clock.joda.interpreter.JodaTimeMInterpreterImpl
import example.shared.lib.util.DateTimeUtils

trait JodaTimeMTestOps {
  implicit class JodaTimeMTestOps[R, A](effects: Eff[R, A]) {
    def testRunjodaTimeM[U](
      implicit m1: Member.Aux[JodaTimeM, R, U]
    ) = {
      val interpreter = new JodaTimeMInterpreterImpl(new JodaTimeUtilsImpl)
      interpreter.run(effects)
    }
  }
}

object JodaTimeMTestEffect extends JodaTimeMTestOps {}
