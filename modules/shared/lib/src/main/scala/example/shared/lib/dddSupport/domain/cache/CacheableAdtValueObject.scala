package example.shared.lib.dddSupport.domain.cache

import cats.Eq
import example.shared.lib.dddSupport.domain.ValueObject
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._

import scalaz.\/

trait CacheableAdtValueObject[A <: ValueObject] {
  implicit val eq: Eq[A] = Eq.fromUniversalEquals
  implicit val encoder: Encoder[A]
  implicit val decoder: Decoder[A]
  def encode(v: A): String            = v.asJson.noSpaces
  def decode(v: String): \/[Error, A] = \/.fromEither(io.circe.parser.decode[A](v))
}
