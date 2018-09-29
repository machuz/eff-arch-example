package example.shared.lib.pimp

import com.github.nscala_time.time
import com.github.nscala_time.time.Imports

import org.joda.time.DateTime
import org.joda.time.LocalDate

import example.shared.lib.test.AbstractSpecification

class RichDateTimeSpec extends AbstractSpecification {

  import example.shared.lib.pimp.RichDateTime._
  val datetime: DateTime = new DateTime(2011, 10, 23, 3, 50, 1, 2) // 2011/10/23 03:50:1.2

  "RichDateTime" can {

    "toUnixTime" in {
      val ret: Long = datetime.toUnixTime
      ret must be(1319341801L)
    }

    "toMonthBegin" in {
      val ret: DateTime = datetime.toMonthBegin
      ret.toString must be("2011-10-01T00:00:00.000Z")
    }

    "toNextMonthBegin" in {
      val ret: DateTime = datetime.toNextMonthBegin
      ret.toString must be("2011-11-01T00:00:00.000Z")
    }

    "toUTC" in {
      val ret: DateTime = datetime.toUTC
      ret.toString must be("2011-10-23T03:50:01.002Z") // default UTCなので変わらず
    }

    "toJST" in {
      val ret: DateTime = datetime.toJST
      ret.toString must be("2011-10-23T12:50:01.002+09:00")
    }

    "yesterday" in {
      val ret: DateTime = datetime.yesterday
      ret.toString must be("2011-10-22T00:00:00.000Z")
    }

    "today" in {
      val ret: DateTime = datetime.today
      ret.toString must be("2011-10-23T00:00:00.000Z")
    }

    "lastWeek" in {
      val ret: Seq[LocalDate] = datetime.lastWeek
      ret.map(_.toString) must be(
        IndexedSeq(
          "2011-10-10",
          "2011-10-11",
          "2011-10-12",
          "2011-10-13",
          "2011-10-14",
          "2011-10-15",
          "2011-10-16"
        )
      )
    }

    "toMondayLastWeek" in {
      val ret = datetime.toMondayLastWeek
      ret.toString must be("2011-10-10")
    }

    "toSundayLastWeek" in {
      val ret = datetime.toSundayLastWeek
      ret.toString must be("2011-10-16")
    }

    "thisWeek" in {
      val ret: Seq[LocalDate] = datetime.thisWeek
      ret.map(_.toString) must be(
        IndexedSeq(
          "2011-10-17",
          "2011-10-18",
          "2011-10-19",
          "2011-10-20",
          "2011-10-21",
          "2011-10-22",
          "2011-10-23"
        )
      )
    }

    "toMondayThisWeek" in {
      val ret = datetime.toMondayThisWeek
      ret.toString must be("2011-10-17")
    }

    "toSundayThisWeek" in {
      val ret = datetime.toSundayThisWeek
      ret.toString must be("2011-10-23")
    }

    "nextWeek" in {
      val ret: Seq[LocalDate] = datetime.nextWeek
      ret.map(_.toString) must be(
        IndexedSeq(
          "2011-10-24",
          "2011-10-25",
          "2011-10-26",
          "2011-10-27",
          "2011-10-28",
          "2011-10-29",
          "2011-10-30"
        )
      )
    }

    "toMondayNextWeek" in {
      val ret = datetime.toMondayNextWeek
      ret.toString must be("2011-10-24")
    }

    "toSundayNextWeek" in {
      val ret = datetime.toSundayNextWeek
      ret.toString must be("2011-10-30")
    }

    "setTimeZone" in {
      val ret = datetime.setTimeZone("Asia/Tokyo")
      ret.toString must be("2011-10-23T03:50:01.002+09:00")
    }

  }

}
