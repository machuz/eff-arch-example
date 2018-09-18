package example.shared.lib.dddSupport.domain.cache

import cats.Eq
import example.shared.lib.dddSupport.domain.ValueObject
import io.circe._
import io.circe.syntax._
import shapeless.{ ::, Generic, HNil, Lazy }

import scalaz.\/

trait CacheableValueObject[A <: ValueObject] {
  def deriveValueObjectEncoder(implicit encode: Lazy[ValueObjectEncoder[A]]): Encoder[A] =
    encode.value
  def deriveValueObjectDecoder(implicit decode: Lazy[ValueObjectDecoder[A]]): Decoder[A] =
    decode.value
  implicit val eq: Eq[A] = Eq.fromUniversalEquals
  implicit val encoder: Encoder[A]
  implicit val decoder: Decoder[A]
  def encode(v: A): String            = v.asJson.noSpaces
  def decode(v: String): \/[Error, A] = \/.fromEither(io.circe.parser.decode[A](v))
}

abstract class ValueObjectDecoder[A] extends Decoder[A]

object ValueObjectDecoder {
  implicit def decodeValueObject[A <: ValueObject, R](
    implicit gen: Lazy[Generic.Aux[A, R :: HNil]],
    decode: Decoder[R]
  ): ValueObjectDecoder[A] = new ValueObjectDecoder[A] {
    override def apply(c: HCursor): Decoder.Result[A] =
      decode(c) match {
        case Right(value) => Right(gen.value.from(value :: HNil))
        case l @ Left(_)  => l.asInstanceOf[Decoder.Result[A]]
      }
  }
}

abstract class ValueObjectEncoder[A] extends Encoder[A]

object ValueObjectEncoder {
  implicit def encodeValueObject[A <: ValueObject, R](
    implicit gen: Lazy[Generic.Aux[A, R :: HNil]],
    encode: Encoder[R]
  ): ValueObjectEncoder[A] = new ValueObjectEncoder[A] {
    override def apply(a: A): Json =
      encode(gen.value.to(a).head)
  }
}
