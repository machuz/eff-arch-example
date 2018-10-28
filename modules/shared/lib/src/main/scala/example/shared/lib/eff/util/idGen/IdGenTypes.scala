package example.shared.lib.eff.util.idGen

import org.atnos.eff.|=

trait IdGenTypes {

  type _idgen[R] = IdGen |= R

}
