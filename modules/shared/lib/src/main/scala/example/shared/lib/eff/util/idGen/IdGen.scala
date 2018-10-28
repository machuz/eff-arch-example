package example.shared.lib.eff.util.idGen

import example.shared.lib.dddSupport.domain.{ IdGenerator, Identifier }

sealed abstract class IdGen[+A]

object IdGen extends IdGenCreation {

  case class Generate[A <: Identifier[String]](
    generator: IdGenerator[A]
  ) extends IdGen[A]
}
