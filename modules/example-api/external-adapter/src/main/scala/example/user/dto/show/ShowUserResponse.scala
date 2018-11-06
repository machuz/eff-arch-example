package example.user.dto.show

import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

import java.time.ZonedDateTime

case class ShowUserResponse(
  userId: String,
  name: Option[String],
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
) { self =>
  def toJson: Json = self.asJson
}

object ShowUserResponse {}
