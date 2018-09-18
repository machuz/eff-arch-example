package example.shared.lib.eff.util.idGen

import org.atnos.eff.|=

object IdGenTypes {

  type _idgen[R] = IdGen |= R

}
