package example.shared.lib.eff.util.clock.joda

import org.atnos.eff.Eff
import org.joda.time.DateTime

import jp.eigosapuri.es.shared.lib.eff.util.clock.joda.JodaTimeM.Now
import jp.eigosapuri.es.shared.lib.eff.util.clock.joda.JodaTimeMTypes._jodaTimem

trait JodaTimeMCreation {

  def now[R: _jodaTimem]: Eff[R, DateTime] =
    Eff.send(Now)

}
