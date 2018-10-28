package example.shared.lib.eff.util.idGen

import org.atnos.eff.Eff

import example.shared.lib.dddSupport.domain.{ IdGenerator, Identifier }
import example.shared.lib.eff.util.idGen.IdGen.Generate

trait IdGenCreation extends IdGenTypes {

  def generate[T <: Identifier[String], R: _idgen](generator: IdGenerator[T]): Eff[R, T] =
    Eff.send[IdGen, R, T](Generate[T](generator))
}
