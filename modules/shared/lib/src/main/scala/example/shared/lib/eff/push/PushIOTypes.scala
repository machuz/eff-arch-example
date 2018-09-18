package example.shared.lib.eff.push

import org.atnos.eff.|=

import scalaz.{ @@, Tag }

object PushIOTypes {

  type _pushio[R] = PushIO |= R

}
