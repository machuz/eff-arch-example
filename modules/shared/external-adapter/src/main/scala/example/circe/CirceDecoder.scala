package example.circe

import cats.data.Validated.{Invalid, Valid}
import example.shared.lib.dddSupport.Error.FormValidationError
import io.circe.Decoder
import io.circe.parser._

abstract class CirceDecoder[A] extends CirceErrorConverter {

  protected implicit def decoder: Decoder[A]

  def decode(jsonStr: String): Either[FormValidationError, A] =
    decodeAccumulating(jsonStr) match {
      case Valid(value)    => Right(value)
      case Invalid(errors) => Left(FormValidationError(errors.map(convert).toList))
    }
}
