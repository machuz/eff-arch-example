package example.shared.lib.eff.util.clock.joda.interpreter

import com.google.inject.Inject

import org.atnos.eff._
import org.atnos.eff.syntax.eff._
import org.atnos.eff.interpret._

import example.shared.lib.eff.util.clock.joda.{ JodaTimeM, JodaTimeUtils }
import example.shared.lib.eff.util.clock.joda.JodaTimeM.Now

abstract class JodaTimeMInterpreter {
  def run[R, U, A](effects: Eff[R, A])(
    implicit m: Member.Aux[JodaTimeM, R, U]
  ): Eff[U, A]
}

class JodaTimeMInterpreterImpl @Inject()(
  u: JodaTimeUtils
) extends JodaTimeMInterpreter {

  def run[R, U, A](effects: Eff[R, A])(
    implicit m: Member.Aux[JodaTimeM, R, U]
  ): Eff[U, A] = {
    translate(effects)(new Translate[JodaTimeM, U] {
      def apply[X](j: JodaTimeM[X]): Eff[U, X] = {
        j match {
          case Now =>
            for {
              now <- u.now.pureEff[U]
            } yield now.asInstanceOf[X]
        }
      }
    })
  }
}
