package example

import java.time.ZonedDateTime

package object user {

  import io.circe._
  import io.circe.syntax._
  import io.circe.generic.auto._

  case class IndexResponse(users: Seq[ShowUserResponse])

  case class ShowUserRequest(userId: String)
  case class ShowUserResponse(
    userId: String,
    name: Option[String],
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime
  )

  case class CreateUserRequest(name: Option[String])
  case class CreateUserResponse(
    userId: String,
    name: Option[String],
    createdAt: ZonedDateTime,
    updatedAt: ZonedDateTime
  )

}
