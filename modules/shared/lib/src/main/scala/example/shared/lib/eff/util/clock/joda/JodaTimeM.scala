package example.shared.lib.eff.util.clock.joda

import org.joda.time.DateTime

sealed abstract class JodaTimeM[+A]

object JodaTimeM extends JodaTimeMCreation {

  case object Now extends JodaTimeM[DateTime]

}
