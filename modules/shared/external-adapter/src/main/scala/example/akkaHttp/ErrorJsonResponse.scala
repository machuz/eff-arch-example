package example.akkaHttp

import akka.http.scaladsl.model.{ HttpHeader, HttpResponse, StatusCode }
import example.shared.lib.dddSupport.ErrorCode
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Encoder

case class ErrorResponse(
  errors: Seq[ErrorJsonModel]
)

object ErrorResponse extends OutputJson[ErrorResponse] {

  override implicit val encoder: Encoder[ErrorResponse] = deriveEncoder

  def apply(code: ErrorCode, message: String): ErrorResponse = {
    ErrorResponse(
      errors = Seq(ErrorJsonModel(code.value, message))
    )
  }
}

case class ErrorJsonModel(
  code: String,
  message: String
)

object ErrorJsonModel extends OutputJson[ErrorJsonModel] {
  override implicit val encoder: Encoder[ErrorJsonModel] = deriveEncoder
}
