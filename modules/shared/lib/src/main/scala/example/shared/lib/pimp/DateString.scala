package example.shared.lib.pimp

import cats.implicits._

import org.joda.time.format.{ DateTimeFormat, DateTimeFormatter }
import org.joda.time.{ DateTime, LocalDate }

import scala.language.implicitConversions
import scala.util.matching.Regex

/**
  * 文字列をDate系の型へ変換する
  * @param underlying underlying
  */
case class DateString(underlying: String) {

  def toDateTime: DateTime = {
    val formatter = DateStringFormat.valueOf(underlying).toDateTimeFormatter
    formatter.parseDateTime(underlying)
  }

  def toDateTimeOpt: Option[DateTime] = {
    try {
      val formatter = DateStringFormat.valueOf(underlying).toDateTimeFormatter
      formatter.parseDateTime(underlying).some
    } catch {
      case _: Throwable => none
    }
  }

  def toLocalDate: LocalDate = {
    val formatter = DateStringFormat.valueOf(underlying).toDateTimeFormatter
    formatter.parseDateTime(underlying).toLocalDate // タイムゾーン考慮のためparseLocalDateは使用しません
  }
}

object DateString {
  implicit def stringToDateString(s: String): DateString = DateString(s)
}

/**
  * DateStringで変換するStringFormat
  * @param fmt 日時フォーマット
  * @param regex 日時フォーマットのRegex
  */
sealed abstract class DateStringFormat(val fmt: String, val regex: Regex) {
  def toDateTimeFormatter: DateTimeFormatter = DateTimeFormat.forPattern(fmt)
}

private[pimp] object DateStringFormat extends Serializable {

  // 文字列全体にマッチするパターンにする必要があるので全体をグループ化(
  // グループ化したものを扱っている関係上,(x|y)とするとバグるので冗長に書いている
  case object ISO8601
    extends DateStringFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ", """(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{3}Z)""".r)

  case object ISO8601_1
    extends DateStringFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSSZZ",
      """(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{3}\+\d{2}:\d{2})""".r
    )

  case object ISO8601_2
    extends DateStringFormat(
      "yyyy-MM-dd'T'HH:mm:ss.SSSZZ",
      """(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}.\d{3}\-\d{2}:\d{2})""".r
    )
  //2017-07-14T  16:26:35+09:00
  case object ISO8601_3
    extends DateStringFormat("yyyy-MM-dd'T'HH:mm:ssZZ", """(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\+\d{2}:\d{2})""".r)

  case object ISO8601_4
    extends DateStringFormat("yyyy-MM-dd'T'HH:mm:ssZZ", """(\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}\-\d{2}:\d{2})""".r)

  case object p1 extends DateStringFormat("yyyyMMdd", """(\d{4}\d{1,2}\d{1,2})""".r)

  case object p2 extends DateStringFormat("yyyyMMddHHmmss", """(\d{4}\d{1,2}\d{1,2}\d{1,2}\d{1,2}\d{1,2})""".r)

  case object p3 extends DateStringFormat("yyyy/MM/dd", """(\d{4}/\d{1,2}/\d{1,2})""".r)

  case object p4
    extends DateStringFormat("yyyy/MM/dd HH:mm:ss", """(\d{4}/\d{1,2}/\d{1,2} \d{1,2}:\d{1,2}:\d{1,2})""".r)

  case object p5 extends DateStringFormat("yyyy-MM-dd", """(\d{4}-\d{1,2}-\d{1,2})""".r)

  case object p6
    extends DateStringFormat("yyyy-MM-dd HH:mm:ss", """(\d{4}-\d{1,2}-\d{1,2} \d{1,2}:\d{1,2}:\d{1,2})""".r)

  val values: Seq[DateStringFormat] = Seq(ISO8601, p1, p2, p3, p4, p5, p6)

  def valueOf(str: String): DateStringFormat = {
    str match {
      case ISO8601.regex(_)   => ISO8601
      case ISO8601_1.regex(_) => ISO8601_1
      case ISO8601_2.regex(_) => ISO8601_2
      case ISO8601_3.regex(_) => ISO8601_3
      case ISO8601_4.regex(_) => ISO8601_4
      case p1.regex(_)        => p1
      case p2.regex(_)        => p2
      case p3.regex(_)        => p3
      case p4.regex(_)        => p4
      case p5.regex(_)        => p5
      case p6.regex(_)        => p6
      case _                  => throw new IllegalArgumentException(s"this format[$str] is not supported")
    }
  }

}
