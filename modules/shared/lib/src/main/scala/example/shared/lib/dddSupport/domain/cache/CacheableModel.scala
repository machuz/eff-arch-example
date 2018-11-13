package example.shared.lib.dddSupport.domain.cache

import cats._
import io.circe._
import io.circe.syntax._

trait CacheableModel[A] {
  implicit val eq: Eq[A] = Eq.fromUniversalEquals
  implicit val encoder: Encoder[A]
  implicit val decoder: Decoder[A]
  def encode(v: A): String                = v.asJson.noSpaces
  def decode(v: String): Either[Error, A] = io.circe.parser.decode[A](v)
}
