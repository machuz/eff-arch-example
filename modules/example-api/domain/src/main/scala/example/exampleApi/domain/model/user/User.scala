package example.exampleApi.domain.model.user
import example.shared.lib.dddSupport.domain.Entity

import java.time.ZonedDateTime

case class User(
  id: UserId,
  name: Option[String],
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
) extends Entity[UserId] {
  override val identifier: UserId = id
}
