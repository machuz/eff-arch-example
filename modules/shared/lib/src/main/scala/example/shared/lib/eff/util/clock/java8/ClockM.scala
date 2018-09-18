package example.shared.lib.eff.util.clock.java8

import java.time.{ LocalDateTime, ZoneOffset, ZonedDateTime }

sealed abstract class ClockM[+A]

object ClockM {

  case object LocalNow extends ClockM[LocalDateTime]

  case class Local(
    year: Int = 1970,
    month: Int = 1,
    dayOfMonth: Int = 1,
    hour: Int = 0,
    min: Int = 0,
    sec: Int = 0
  ) extends ClockM[LocalDateTime]

  case class ZonedNow(
    tz: ZoneOffset = ZoneOffset.UTC
  ) extends ClockM[ZonedDateTime]

  case class Zoned(
    year: Int = 1970,
    month: Int = 1,
    dayOfMonth: Int = 1,
    hour: Int = 0,
    min: Int = 0,
    sec: Int = 0,
    tz: ZoneOffset = ZoneOffset.UTC
  ) extends ClockM[ZonedDateTime]

  case class ThisWeekMonday(tz: ZoneOffset = ZoneOffset.UTC) extends ClockM[ZonedDateTime]
  case class ThisWeekSunday(tz: ZoneOffset = ZoneOffset.UTC) extends ClockM[ZonedDateTime]
  case class ContainsThisWeek(d: ZonedDateTime)              extends ClockM[Boolean]
}
