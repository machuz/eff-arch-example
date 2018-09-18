package example.shared.lib.eff.util.clock.java8.interpreter

import org.atnos.eff.interpret.translate
import org.atnos.eff.syntax.eff._
import org.atnos.eff.reader._
import org.atnos.eff.{ Eff, Member, Translate }

import java.time.{ Clock, DayOfWeek, LocalDateTime, ZonedDateTime }

import jp.eigosapuri.es.shared.lib.eff.util.clock.java8.ClockM
import jp.eigosapuri.es.shared.lib.eff.util.clock.java8.ClockM._
import jp.eigosapuri.es.shared.lib.eff.util.clock.java8.ClockMInterpretationTypes._

trait ClockMInterpreter {

  def run[R, U, A](effects: Eff[R, A])(
    implicit m: Member.Aux[ClockM, R, U],
    m1: _readerClock[U]
  ): Eff[U, A] = {
    translate(effects)(new Translate[ClockM, U] {
      def apply[X](c: ClockM[X]): Eff[U, X] = {
        for {
          clock <- ask[U, Clock]
          x <- {
            (c match {
              case LocalNow =>
                LocalDateTime.now(clock)
              case Local(year, month, day, hour, min, sec) =>
                LocalDateTime.of(year, month, day, hour, min, sec)
              case ZonedNow(tz) =>
                ZonedDateTime.now(clock.withZone(tz))
              case Zoned(year, month, dayOfMonth, hour, min, sec, tz) =>
                ZonedDateTime.of(year, month, dayOfMonth, hour, min, sec, 0, tz)
              case ThisWeekMonday(tz) =>
                ZonedDateTime.now(clock.withZone(tz)).`with`(DayOfWeek.MONDAY)
              case ThisWeekSunday(tz) =>
                ZonedDateTime.now(clock.withZone(tz)).`with`(DayOfWeek.SUNDAY)
              case ContainsThisWeek(d) =>
                val now    = ZonedDateTime.now(clock.withZone(d.getOffset))
                val monday = now.`with`(DayOfWeek.MONDAY)
                val sunday = now.`with`(DayOfWeek.SUNDAY)
                d.compareTo(monday) > 0 && d.compareTo(sunday) <= 0
            }).pureEff[U].map(_.asInstanceOf[X])
          }
        } yield x
      }
    })
  }
}
