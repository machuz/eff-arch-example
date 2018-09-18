package example.shared.lib.eff.util.idGen

import org.atnos.eff.Eff

import jp.eigosapuri.es.shared.lib.dddSupport.domain.{ IdGenerator, Identifier }
import jp.eigosapuri.es.shared.lib.eff.util.idGen.IdGen.Generate
import jp.eigosapuri.es.shared.lib.eff.util.idGen.IdGenTypes._idgen

trait IdGenCreation {

  def generate[T <: Identifier[String], R: _idgen](generator: IdGenerator[T]): Eff[R, T] =
    Eff.send[IdGen, R, T](Generate[T](generator))
}
