package example.akkaHttp

import io.circe.{ Encoder, Json }
import io.circe.generic.semiauto._

trait OutputJson[A] {
  implicit val encoder: Encoder[A]
}
