package example.user.dto.create

import io.circe
import io.circe.generic.auto._
import io.circe.parser

case class CreateUserRequest(name: Option[String])

object CreateUserRequest {
  def fromJson(jsonStr: String): String => Either[circe.Error, CreateUserRequest] =
    parser.decode[CreateUserRequest]
}
