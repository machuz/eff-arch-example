package example.user

import example.akkaHttp.OutputJson
import example.exampleApi.domain.model.user.User
import io.circe.Json

import java.time.ZonedDateTime

case class UserJsonModel(
  id: String,
  name: Option[String],
  createdAt: ZonedDateTime,
  updatedAt: ZonedDateTime
) extends OutputJson { self =>

  import io.circe.syntax._
  import io.circe.generic.auto
  override def toJson: Json = self.asJson
}

object UserJsonModel {

  def convertToJsonModel(domainModel: User): UserJsonModel = {
    UserJsonModel(
      id = domainModel.id.value,
      name = domainModel.name,
      createdAt = domainModel.createdAt,
      updatedAt = domainModel.updatedAt
    )
  }

}
