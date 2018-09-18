package example.shared.lib.pimp

import com.github.nscala_time.time.Imports._

import org.joda.time.{ DateTime, DateTimeZone }

import java.time.DayOfWeek

import scala.language.implicitConversions

import jp.eigosapuri.es.shared.lib.pimp.RichDateTime.YearWeek

case class RichDateTime(underlying: DateTime) {

  /**
    * Unix Timeにする
    *
    * @return
    */
  def toUnixTime: Long = underlying.getMillis / 1000

  /**
    * 月初にする
    * ex.) 2014-10-10 -> 2014-10-01
    *
    * @return
    */
  def toMonthBegin: DateTime = underlying.withDayOfMonth(1).withTimeAtStartOfDay

  /**
    * 翌月月初にする
    * ex.) 2014-10-10 -> 2014-11-01
    *
    * @return
    */
  def toNextMonthBegin: DateTime = underlying.withDayOfMonth(1).plusMonths(1).withTimeAtStartOfDay

  /**
    * UTC時間に変換する
    *
    * @return
    */
  def toUTC: DateTime = {
    val UTC = DateTimeZone.UTC
    underlying.toDateTime(UTC)
  }

  /**
    * JST時間に変換する
    *
    * @return
    */
  def toJST: DateTime = {
    val JST = DateTimeZone.forID("Asia/Tokyo")
    underlying.toDateTime(JST)
  }

  /**
    * 前日の0時へ変換する
    *
    * @return
    */
  def yesterday: DateTime = today - 1.days

  /**
    * 当日の0時へ変換する
    *
    * @return
    */
  def today: DateTime = underlying.toLocalDate.toDateTimeAtStartOfDay

  /**
    * 翌日の0時へ変換する
    *
    * @return
    */
  def tomorrow: DateTime = today + 1.day

  /**
    * 先週の月曜〜日曜の日付リストを取得
    *
    * @return
    */
  def lastWeek: Seq[LocalDate] = {
    val monday = underlying.minusWeeks(1).withDayOfWeek(DayOfWeek.MONDAY.getValue)
    take7Days(monday)
  }

  /**
    * 先週の月曜へ変換
    *
    * @return
    */
  def toMondayLastWeek: LocalDate = {
    val monday = underlying.minusWeeks(1).withDayOfWeek(DayOfWeek.MONDAY.getValue).toLocalDate
    monday
  }

  /**
    * 先週の日曜へ変換
    *
    * @return
    */
  def toSundayLastWeek: LocalDate = {
    val sunday = underlying.minusWeeks(1).withDayOfWeek(DayOfWeek.MONDAY.getValue).toLocalDate + 6.days
    sunday
  }

  /**
    * 今週の月曜〜日曜の日付リストを取得
    *
    * @return
    */
  def thisWeek: Seq[LocalDate] = {
    val monday = underlying.withDayOfWeek(DayOfWeek.MONDAY.getValue)
    take7Days(monday)
  }

  /**
    * 今週の月曜へ変換
    *
    * @return
    */
  def toMondayThisWeek: LocalDate = {
    val monday = underlying.withDayOfWeek(DayOfWeek.MONDAY.getValue).toLocalDate
    monday
  }

  /**
    * 今週の日曜へ変換
    *
    * @return
    */
  def toSundayThisWeek: LocalDate = {
    val sunday = underlying.withDayOfWeek(DayOfWeek.MONDAY.getValue).toLocalDate + 6.days
    sunday
  }

  /**
    * 来週の月曜〜日曜の日付リストを取得
    *
    * @return
    */
  def nextWeek: Seq[LocalDate] = {
    val monday = underlying.plusWeeks(1).withDayOfWeek(DayOfWeek.MONDAY.getValue)
    take7Days(monday)
  }

  /**
    * 来週の月曜へ変換
    *
    * @return
    */
  def toMondayNextWeek: LocalDate = {
    val monday = underlying.plusWeeks(1).withDayOfWeek(DayOfWeek.MONDAY.getValue).toLocalDate
    monday
  }

  /**
    * 来週の日曜へ変換
    *
    * @return
    */
  def toSundayNextWeek: LocalDate = {
    val sunday = underlying.plusWeeks(1).withDayOfWeek(DayOfWeek.MONDAY.getValue).toLocalDate + 6.days
    sunday
  }

  /**
    * 対象日を含め7日間の日付リストを取得
    *
    * @return
    */
  private def take7Days(d: DateTime): Seq[LocalDate] = {
    val targetDay = d.toLocalDate
    for { i <- 0 to 6 } yield targetDay + i.days
  }

  /**
    * 時間は変換せず、timezoneだけ変更する
    *
    * @return
    */
  def setTimeZone(zone: String): DateTime = {
    val tz = DateTimeZone.forID(zone)
    underlying.toLocalDateTime.toDateTime(tz)
  }

  /**
    * YEARWEEK(time, 7)と同等（本年の月曜日を含む週を第一週と定義）
    *
    * @return
    */
  def toYearWeek: YearWeek = {
    val midnightTime = underlying.withMillisOfDay(0)
    val year         = midnightTime.withDayOfWeek(1).getYear
    val firstWeekMonday = midnightTime.withYear(year).withDayOfYear(1).withDayOfWeek(1) match {
      case date if date.getYear == year => date
      case date                         => date.plusWeeks(1)
    }
    val weekNumber = (midnightTime.withDayOfWeek(1).getDayOfYear - firstWeekMonday.getDayOfYear) / 7 + 1
    YearWeek(year = year, weekNumber = weekNumber)
  }

}

object RichDateTime {
  implicit def dateTimeToRich(d: DateTime): RichDateTime = RichDateTime(d)
  case class YearWeek(year: Int, weekNumber: Int)
}
