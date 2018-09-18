package example.shared.lib.pimp

import jp.eigosapuri.es.shared.lib.pimp.DateString._
import jp.eigosapuri.es.shared.lib.pimp.RichDateTime._
import jp.eigosapuri.es.shared.lib.util.DateTimeUtils
import org.joda.time.{ DateTime, LocalDate }

import example.shared.lib.test.AbstractSpecification

class DateStringSpec extends AbstractSpecification {

  val datetime: DateTime = new DateTime(2011, 10, 23, 3, 50, 1, 2)

  "DateString" should {

    "toDateTime" must {

      "toDateTime&toLocalDate[UTC] (ISO8601)" in {
        val dt: String = datetime.toString

        val ret1: DateTime  = dt.toDateTime
        val ret2: LocalDate = dt.toLocalDate
        ret1.toString must be("2011-10-23T03:50:01.002Z")
        ret2.toString must be("2011-10-23")
      }

      // 返り値は常にUTC
      "toDateTime&toLocalDate[JST] (ISO8601)" in {
        val dt: String = datetime.setTimeZone(DateTimeUtils.TIMEZONE_JST.getID).toString

        val ret1: DateTime  = dt.toDateTime
        val ret2: LocalDate = dt.toLocalDate
        ret1.toString must be("2011-10-22T18:50:01.002Z")
        ret2.toString must be("2011-10-22")
      }

      "toDateTime&toLocalDate[yyyy-MM-dd'T'HH:mm:ss.SSSZ] (ISO8601)" in {
        val dt: String      = datetime.toString("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
        val ret1: DateTime  = dt.toDateTime
        val ret2: LocalDate = dt.toLocalDate
        ret1.toString must be("2011-10-23T03:50:01.002Z")
        ret2.toString must be("2011-10-23")
      }

      "toDateTime&toLocalDate[yyyy-MM-dd'T'HH:mm:ssZ] (ISO8601)" in {
        val dt: String      = datetime.toString("yyyy-MM-dd'T'HH:mm:ssZZ")
        val ret1: DateTime  = dt.toDateTime
        val ret2: LocalDate = dt.toLocalDate
        ret1.toString must be("2011-10-23T03:50:01.000Z")
        ret2.toString must be("2011-10-23")
      }

      "toDateTime&toLocalDate[yyyyMMdd]" in {
        val dt: String      = datetime.toString("yyyyMMdd")
        val ret1: DateTime  = dt.toDateTime
        val ret2: LocalDate = dt.toLocalDate
        ret1.toString must be("2011-10-23T00:00:00.000Z")
        ret2.toString must be("2011-10-23")
      }

      "toDateTime&toLocalDate[yyyyMMddHHmmss]" in {
        val dt: String      = datetime.toString("yyyyMMddHHmmss")
        val ret1: DateTime  = dt.toDateTime
        val ret2: LocalDate = dt.toLocalDate
        ret1.toString must be("2011-10-23T03:50:01.000Z")
        ret2.toString must be("2011-10-23")
      }

      "toDateTime&toLocalDate[yyyy/MM/dd]" in {
        val dt: String      = datetime.toString("yyyy/MM/dd")
        val ret1: DateTime  = dt.toDateTime
        val ret2: LocalDate = dt.toLocalDate
        ret1.toString must be("2011-10-23T00:00:00.000Z")
        ret2.toString must be("2011-10-23")
      }

      "toDateTime&toLocalDate[yyyy/MM/dd HH:mm:ss]" in {
        val dt: String      = datetime.toString("yyyy/MM/dd HH:mm:ss")
        val ret1: DateTime  = dt.toDateTime
        val ret2: LocalDate = dt.toLocalDate
        ret1.toString must be("2011-10-23T03:50:01.000Z")
        ret2.toString must be("2011-10-23")
      }

      "toDateTime&toLocalDate[yyyy-MM-dd]" in {
        val dt: String      = datetime.toString("yyyy-MM-dd")
        val ret1: DateTime  = dt.toDateTime
        val ret2: LocalDate = dt.toLocalDate
        ret1.toString must be("2011-10-23T00:00:00.000Z")
        ret2.toString must be("2011-10-23")
      }

      "toDateTime&toLocalDate[yyyy-MM-dd HH:mm:ss]" in {
        val dt: String      = datetime.toString("yyyy-MM-dd HH:mm:ss")
        val ret1: DateTime  = dt.toDateTime
        val ret2: LocalDate = dt.toLocalDate
        ret1.toString must be("2011-10-23T03:50:01.000Z")
        ret2.toString must be("2011-10-23")
      }

    }
  }
}
