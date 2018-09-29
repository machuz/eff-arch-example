package example.shared.lib.util

import org.joda.time.{ DateTime, DateTimeZone, Interval, LocalDate }

trait DateTimeUtils {
  val DEFAULT_TIMEZONE: DateTimeZone

  val TIMEZONE_JST: DateTimeZone

  def dateTimeNow: DateTime

  def dateTimeNow(d: DateTime, timezone: DateTimeZone = DEFAULT_TIMEZONE): DateTime

  def localDateNow(d: DateTime, timezone: DateTimeZone = DEFAULT_TIMEZONE): LocalDate

  def utcDateTimeNow(d: DateTime = dateTimeNow): DateTime

  def utcLocalDateNow(d: DateTime = dateTimeNow): LocalDate

  def jstDateTimeNow(d: DateTime = dateTimeNow): DateTime

  def jstLocalDateNow(d: DateTime = dateTimeNow): LocalDate

  def dateTimeUTC(instant: Long): DateTime

  def dateTimeJST(instant: Long): DateTime

  def minDateTime(timezone: DateTimeZone = DEFAULT_TIMEZONE): DateTime

  def maxDateTime(timezone: DateTimeZone = DEFAULT_TIMEZONE): DateTime

  def thisWeekMonday(d: DateTime = dateTimeNow, timezone: DateTimeZone = DEFAULT_TIMEZONE): DateTime

  def thisWeekSunday(d: DateTime = dateTimeNow, timezone: DateTimeZone = DEFAULT_TIMEZONE): DateTime

  def thisWeekInterval(d: DateTime = dateTimeNow, timezone: DateTimeZone = DEFAULT_TIMEZONE): Interval

  def containsThisWeekUTC(d: DateTime): Boolean

  def containsThisWeekJST(d: DateTime): Boolean
}
