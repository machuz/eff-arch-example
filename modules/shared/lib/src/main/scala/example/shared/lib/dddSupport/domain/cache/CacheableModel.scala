package example.shared.lib.dddSupport.domain.cache

import cats.Eq
import io.circe._
import io.circe.syntax._

import scalaz.\/

trait CacheableModel[A] {
  implicit val eq: Eq[A] = Eq.fromUniversalEquals
  implicit val encoder: Encoder[A]
  implicit val decoder: Decoder[A]
  def encode(v: A): String            = v.asJson.noSpaces
  def decode(v: String): \/[Error, A] = \/.fromEither(io.circe.parser.decode[A](v))
}
