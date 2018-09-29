package example.shared.lib.test

import com.eaio.uuid.UUID

import org.joda.time.{ DateTime, DateTimeConstants, DateTimeZone, Interval, LocalDate }

import scala.concurrent.ExecutionContext

import example.shared.lib.util.DateTimeUtils
import example.shared.lib.dddSupport.domain.{ Identifier, UUIDIdGenerator }

/**
  * UseCase test用の抽象クラス
  */
abstract class AbstractUseCaseSpecification extends AbstractSpecification {

  implicit val ec: ExecutionContext = ExecutionContext.global

  // TODO: 全部DIするようにして消す
  org.joda.time.DateTimeUtils.setCurrentMillisFixed(ConstantTestObject[DateTime].getMillis)

  // TODO: State使ってうまい感じにする方法考える
  implicit val dateTimeUtils: DateTimeUtils = new DateTimeUtils {

    override val DEFAULT_TIMEZONE: DateTimeZone = DateTimeZone.UTC

    val TIMEZONE_JST: DateTimeZone = DateTimeZone.forID("Asia/Tokyo")

    def dateTimeNow: DateTime =
      ConstantTestObject[DateTime].withZone(DateTimeZone.UTC)

    def dateTimeNow(d: DateTime = ConstantTestObject[DateTime], timezone: DateTimeZone = DateTimeZone.UTC): DateTime =
      d.withZone(timezone)

    def localDateNow(
      d: DateTime = ConstantTestObject[DateTime],
      timezone: DateTimeZone = DateTimeZone.UTC
    ): LocalDate =
      dateTimeNow(d = d).withZone(timezone).toLocalDate

    def utcDateTimeNow(d: DateTime = ConstantTestObject[DateTime]): DateTime =
      dateTimeNow(d = d)

    def utcLocalDateNow(d: DateTime = ConstantTestObject[DateTime]): LocalDate =
      dateTimeNow(d = d).toLocalDate

    def jstDateTimeNow(d: DateTime = ConstantTestObject[DateTime]): DateTime =
      dateTimeNow(timezone = TIMEZONE_JST, d = d)

    def jstLocalDateNow(d: DateTime = ConstantTestObject[DateTime]): LocalDate =
      dateTimeNow(timezone = TIMEZONE_JST, d = d).toLocalDate

    def dateTimeUTC(instant: Long): DateTime =
      new DateTime(instant).withZone(DateTimeZone.UTC)

    def dateTimeJST(instant: Long): DateTime =
      new DateTime(instant).withZone(TIMEZONE_JST)

    def minDateTime(timezone: DateTimeZone = DateTimeZone.UTC): DateTime =
      new DateTime(0, 1, 1, 0, 0, 0, DateTimeZone.UTC)

    def maxDateTime(timezone: DateTimeZone = DateTimeZone.UTC): DateTime =
      new DateTime(9999, 1, 1, 0, 0, 0, DateTimeZone.UTC)

    def thisWeekMonday(
      d: DateTime = ConstantTestObject[DateTime],
      timezone: DateTimeZone = DateTimeZone.UTC
    ): DateTime = {
      d.withDayOfWeek(DateTimeConstants.MONDAY)
        .withTime(0, 0, 0, 0)
        .withZone(timezone)
    }

    def thisWeekSunday(
      d: DateTime = ConstantTestObject[DateTime],
      timezone: DateTimeZone = DateTimeZone.UTC
    ): DateTime = {
      d.withDayOfWeek(DateTimeConstants.SUNDAY)
        .withTime(
          DateTimeConstants.HOURS_PER_DAY - 1,
          DateTimeConstants.MINUTES_PER_HOUR - 1,
          DateTimeConstants.SECONDS_PER_MINUTE - 1,
          DateTimeConstants.MILLIS_PER_SECOND - 1
        )
        .withZone(timezone)
    }

    def thisWeekInterval(
      d: DateTime = ConstantTestObject[DateTime],
      timezone: DateTimeZone = DateTimeZone.UTC
    ): Interval = {
      new Interval(
        thisWeekMonday(d = d, timezone = timezone),
        thisWeekSunday(d = d, timezone = timezone)
      )
    }

    def containsThisWeekUTC(d: DateTime): Boolean = {
      thisWeekInterval(timezone = DateTimeZone.UTC).contains(d)
    }

    def containsThisWeekJST(d: DateTime): Boolean = {
      thisWeekInterval(timezone = TIMEZONE_JST).contains(d)
    }
  }
}
