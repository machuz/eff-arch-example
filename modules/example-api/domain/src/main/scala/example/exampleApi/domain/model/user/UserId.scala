package example.exampleApi.domain.model.user

import org.atnos.eff.Eff

import example.shared.lib.dddSupport.domain.{ IdGenerator, Identifier }
import example.shared.lib.eff._
import example.shared.lib.eff.util.idGen.IdGen

case class UserId(value: String) extends Identifier[String]

object UserId {

  import io.circe._
  import io.circe.syntax._
  import io.circe.generic.auto._

  def generate[R: _idgen]: Eff[R, UserId] = {
    val gen = new IdGenerator[UserId] {
      override def generate(value: String): UserId = UserId(value)
    }
    IdGen.generate[UserId, R](gen)
  }

}
