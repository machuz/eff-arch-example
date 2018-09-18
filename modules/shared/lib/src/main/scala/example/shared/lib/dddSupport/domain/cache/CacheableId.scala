package example.shared.lib.dddSupport.domain.cache

import cats.Eq
import example.shared.lib.dddSupport.domain.Identifier
import io.circe._
import io.circe.syntax._
import shapeless.{ ::, Generic, HNil, Lazy }

import scalaz.\/

trait CacheableId[A] {
  def deriveIdentifierEncoder(implicit encode: Lazy[IdentifierEncoder[A]]): Encoder[A] = encode.value
  def deriveIdentifierDecoder(implicit decode: Lazy[IdentifierDecoder[A]]): Decoder[A] = decode.value
  implicit val eq: Eq[A]                                                               = Eq.fromUniversalEquals
  implicit def encoder: Encoder[A]
  implicit def decoder: Decoder[A]
  def encode(v: A): String            = v.asJson.noSpaces
  def decode(v: String): \/[Error, A] = \/.fromEither(io.circe.parser.decode[A](v))

}

abstract class IdentifierDecoder[A] extends Decoder[A]

object IdentifierDecoder {
  implicit def decodeIdentifier[A <: Identifier[_], R](
    implicit gen: Lazy[Generic.Aux[A, R :: HNil]],
    decode: Decoder[R]
  ): IdentifierDecoder[A] = new IdentifierDecoder[A] {
    override def apply(c: HCursor): Decoder.Result[A] =
      decode(c) match {
        case Right(value) => Right(gen.value.from(value :: HNil))
        case l @ Left(_)  => l.asInstanceOf[Decoder.Result[A]]
      }
  }
}

abstract class IdentifierEncoder[A] extends Encoder[A]

object IdentifierEncoder {
  implicit def encodeIdentifier[A <: Identifier[_], R](
    implicit gen: Lazy[Generic.Aux[A, R :: HNil]],
    encode: Encoder[R]
  ): IdentifierEncoder[A] = new IdentifierEncoder[A] {
    override def apply(a: A): Json =
      encode(gen.value.to(a).head)
  }
}
