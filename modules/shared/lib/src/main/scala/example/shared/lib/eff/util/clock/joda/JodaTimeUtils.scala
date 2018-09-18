package example.shared.lib.eff.util.clock.joda

import org.joda.time.{ DateTime, DateTimeZone }

abstract class JodaTimeUtils {

  val DEFAULT_TIMEZONE: DateTimeZone = DateTimeZone.UTC

  val TIMEZONE_JST: DateTimeZone = DateTimeZone.forID("Asia/Tokyo")

  def now: DateTime

}

class JodaTimeUtilsImpl extends JodaTimeUtils {
  override def now: DateTime = DateTime.now()
}
