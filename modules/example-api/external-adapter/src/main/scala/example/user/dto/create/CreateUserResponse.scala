package example.user.dto.create

import io.circe.Json
import io.circe.syntax._
import io.circe.generic.auto._

import java.time.ZonedDateTime

case class CreateUserResponse(
  userId: String,
  name: Option[String],
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
) { self =>
  def toJson: Json = self.asJson
}
