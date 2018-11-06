package example.user.dto.show

import io.circe
import io.circe.generic.auto._
import io.circe.parser

case class ShowUserRequest(userId: String)

object ShowUserRequest {
  def fromJson(jsonStr: String): String => Either[circe.Error, ShowUserResponse] =
    parser.decode[ShowUserResponse]
}
