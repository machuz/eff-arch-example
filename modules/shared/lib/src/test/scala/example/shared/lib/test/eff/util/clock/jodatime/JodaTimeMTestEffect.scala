package example.shared.lib.test.eff.util.clock.jodatime

import org.atnos.eff.{ Eff, Member }

import jp.eigosapuri.es.shared.lib.eff.util.clock.joda.{ JodaTimeM, JodaTimeUtilsImpl }
import jp.eigosapuri.es.shared.lib.eff.util.clock.joda.interpreter.JodaTimeMInterpreterImpl
import jp.eigosapuri.es.shared.lib.util.DateTimeUtils

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
