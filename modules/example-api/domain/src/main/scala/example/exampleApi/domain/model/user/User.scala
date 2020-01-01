package example.exampleApi.domain.model.user

import org.atnos.eff.Eff

import example.shared.lib.dddSupport.domain.Entity

import java.time.ZonedDateTime
import example.shared.lib.eff.myEff._
import example.shared.lib.eff.util.clock.java8.ClockM

case class User private (
  id: UserId,
  name: Option[String],
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
) extends Entity[UserId] {
  override val identifier: UserId = id
}

object User {

  def applyEff[R: _idgen: _clockm](
    name: Option[String]
  ): Eff[R, User] = {
    for {
      id  <- UserId.generate[R]
      now <- ClockM.zonedNow[R]()
    } yield
      User(
        id = id,
        name = name,
        createdAt = now,
        updatedAt = now
      )
  }

}
