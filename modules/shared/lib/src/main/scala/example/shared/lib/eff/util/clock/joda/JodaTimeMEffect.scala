package example.shared.lib.eff.util.clock.joda

import org.atnos.eff.{ Eff, Fx, Member }

import example.shared.lib.eff.util.clock.joda.interpreter.JodaTimeMInterpreter

object JodaTimeMInterpretationTypes {
  type JodaTimeMStack = Fx.fx1[JodaTimeM]
}

trait JodaTimeMOps {

  implicit class JodaTimeMOps[R, A](effects: Eff[R, A]) {
    def runJodaTimeM[U](
      implicit interpreter: JodaTimeMInterpreter,
      member1: Member.Aux[JodaTimeM, R, U]
    ): Eff[U, A] =
      interpreter.run(effects)
  }

}

object JodaTimeMEffect extends JodaTimeMOps with JodaTimeMCreation {}
