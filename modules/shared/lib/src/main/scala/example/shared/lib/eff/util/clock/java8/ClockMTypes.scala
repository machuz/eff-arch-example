package example.shared.lib.eff.util.clock.java8

import org.atnos.eff.|=

trait ClockMTypes {

  type _clockm[R] = ClockM |= R

}
