package example.shared.lib.eff.util.clock.joda

import org.atnos.eff.syntax.eff._
import org.joda.time.DateTime
import org.mockito.Mockito

import example.shared.lib.test.AbstractSpecification

import example.shared.lib.eff.util.clock.joda.JodaTimeMInterpretationTypes.JodaTimeMStack
import example.shared.lib.eff.util.clock.joda.interpreter.JodaTimeMInterpreterImpl

class JodaTimeMEffectSpec extends AbstractSpecification {

  import Mockito._
  import example.shared.lib.eff.util.clock.joda.JodaTimeMEffect._

  trait SetUp {
    val now                       = new DateTime(2018, 8, 13, 10, 30)
    implicit val u: JodaTimeUtils = Mockito.mock(classOf[JodaTimeUtils])
    implicit val interpreter      = new JodaTimeMInterpreterImpl(u)
    type R = JodaTimeMStack
  }

  "jodaTimeEffect" should {

    "`now` be successful" in new SetUp {

      when(u.now).thenReturn(now)

      val actual = JodaTimeM.now[R].runJodaTimeM.runPure

      actual must be(Some(now))
      verify(u, times(1)).now
    }
  }

}
