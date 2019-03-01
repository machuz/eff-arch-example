package example.akkaHttp

import example.shared.lib.dddSupport.Error.FormValidationError
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

case class FormErrorResponse(
  errors: Seq[FormErrorJsonModel]
)

object FormErrorResponse extends OutputJson[FormErrorResponse] {

  override implicit val encoder: Encoder[FormErrorResponse] = deriveEncoder

  def apply(formValidationError: FormValidationError): FormErrorResponse = {
    FormErrorResponse(
      errors =
        formValidationError.errors.map(error => FormErrorJsonModel(error.code.value, error.attribute, error.message))
    )
  }
}

case class FormErrorJsonModel(
  code: String,
  attribute: String,
  message: String
)

object FormErrorJsonModel extends OutputJson[FormErrorJsonModel] {
  override implicit val encoder: Encoder[FormErrorJsonModel] = deriveEncoder
}
