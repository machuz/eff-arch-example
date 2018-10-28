package example.shared.lib.eff.util.clock.java8

import org.atnos.eff.Eff

import java.time.{ LocalDateTime, ZoneOffset, ZonedDateTime }

import example.shared.lib.eff.util.clock.java8.ClockM._

trait ClockMCreation extends ClockMTypes {

  def localNow[R: _clockm]: Eff[R, LocalDateTime] =
    Eff.send[ClockM, R, LocalDateTime](LocalNow)

  def local[R: _clockm](
    year: Int = 1970,
    month: Int = 1,
    dayOfMonth: Int = 1,
    hour: Int = 0,
    min: Int = 0,
    sec: Int = 0
  ): Eff[R, LocalDateTime] =
    Eff.send[ClockM, R, LocalDateTime](Local(year, month, dayOfMonth, hour, min, sec))

  def zonedNow[R: _clockm](
    tz: ZoneOffset = ZoneOffset.UTC
  ): Eff[R, ZonedDateTime] =
    Eff.send[ClockM, R, ZonedDateTime](ZonedNow(tz))

  def zoned[R: _clockm](
    year: Int = 1970,
    month: Int = 1,
    dayOfMonth: Int = 1,
    hour: Int = 0,
    min: Int = 0,
    sec: Int = 0,
    tz: ZoneOffset = ZoneOffset.UTC
  ): Eff[R, ZonedDateTime] =
    Eff.send[ClockM, R, ZonedDateTime](Zoned(year, month, dayOfMonth, hour, min, sec, tz))

  def thisWeekMonday[R: _clockm](tz: ZoneOffset = ZoneOffset.UTC): Eff[R, ZonedDateTime] =
    Eff.send[ClockM, R, ZonedDateTime](ThisWeekMonday(tz))

  def thisWeekSunday[R: _clockm](tz: ZoneOffset = ZoneOffset.UTC): Eff[R, ZonedDateTime] =
    Eff.send[ClockM, R, ZonedDateTime](ThisWeekSunday(tz))

  def containsThisWeek[R: _clockm](d: ZonedDateTime): Eff[R, Boolean] =
    Eff.send[ClockM, R, Boolean](ContainsThisWeek(d))

}
