package example.user.dto.index

import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

import example.user.dto.show.ShowUserResponse

case class IndexResponse(users: Seq[ShowUserResponse]) { self =>
  def toJson: Json = self.asJson
}
