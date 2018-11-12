package example.user

import example.akkaHttp.OutputJson
import example.exampleApi.domain.model.user.User
import io.circe.{ Encoder, Json }

import java.time.ZonedDateTime

case class UserJsonModel(
  id: String,
  name: Option[String],
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
)

object UserJsonModel extends OutputJson[UserJsonModel] {
  import io.circe.generic.semiauto._

  override implicit val encoder: Encoder[UserJsonModel] = deriveEncoder

  def convertToJsonModel(domainModel: User): UserJsonModel = {
    UserJsonModel(
      id = domainModel.id.value,
      name = domainModel.name,
      createdAt = domainModel.createdAt,
      updatedAt = domainModel.updatedAt
    )
  }
}
