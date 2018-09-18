package example.shared.lib.util

import org.joda.time.{ DateTime, LocalDate }

import example.shared.lib.test.AbstractSpecification

class DateTimeUtilsSpec extends AbstractSpecification {

  val datetime: DateTime = new DateTime(2011, 10, 23, 23, 50, 1, 2) // 2011/10/23 23:50:1.2

  "DateTimeUtils" can {

    "dateTimeNow" in {
      val ret: DateTime = DateTimeUtils.dateTimeNow(datetime)
      ret.toString must be("2011-10-23T23:50:01.002Z")
    }

    "localDateNow" in {
      val ret: LocalDate = DateTimeUtils.localDateNow(datetime)
      ret.toString must be("2011-10-23")
    }

    "utcDateTimeNow" in {
      val ret: DateTime = DateTimeUtils.utcDateTimeNow(datetime)
      ret.toString must be("2011-10-23T23:50:01.002Z")
    }

    "utcLocalDateNow" in {
      val ret: LocalDate = DateTimeUtils.utcLocalDateNow(datetime)
      ret.toString must be("2011-10-23")
    }

    "jstDateTimeNow" in {
      val ret: DateTime = DateTimeUtils.jstDateTimeNow(datetime)
      ret.toString must be("2011-10-24T08:50:01.002+09:00")
    }

    "jstLocalDateNow" in {
      val ret: LocalDate = DateTimeUtils.jstLocalDateNow(datetime)
      ret.toString must be("2011-10-24")
    }

    "dateTimeUTC" in {
      val ret: DateTime = DateTimeUtils.dateTimeUTC(datetime.getMillis)
      ret.toString must be("2011-10-23T23:50:01.002Z")
    }

    "dateTimeJST" in {
      val ret: DateTime = DateTimeUtils.dateTimeJST(datetime.getMillis)
      ret.toString must be("2011-10-24T08:50:01.002+09:00")
    }
  }

}
