package example.shared.lib.dddSupport

import play.api.libs.json.JsError

sealed trait Error                                 extends Throwable
sealed class ErrorHasUnderlay(underlay: Throwable) extends Error

object ErrorHasUnderlay

object Error {
  import ErrorCode._
  final case object Generic                      extends Error
  final case class ExternalApiError(msg: String) extends Error
  final case class InternalApiError(msg: String) extends Error
  final case class JsonError(jsError: JsError)   extends Error

  final case class NonFatalError(
    underlying: Throwable,
    code: ErrorCode
  ) extends ErrorHasUnderlay(underlying)

  final case class HttpError(msg: String) extends Error

  final case class DatabaseError(
    underlying: Throwable,
    code: ErrorCode = SERVER_ERROR
  ) extends ErrorHasUnderlay(underlying)

  final case class DomainServiceError(code: ErrorCode) extends Error
  final case class UseCaseError(code: ErrorCode)       extends Error
  final case class RepositoryError(code: ErrorCode)    extends Error

  final case class RedisError(code: ErrorCode = REDIS_COMMAND_ERROR) extends Error

  final case class InvalidRequestError(
    msg: String,
    req: String,
    code: ErrorCode = INVALID_REQUEST
  ) extends Error

  final case class ThirdPartyServiceError(
    underlying: Throwable,
    code: ErrorCode
  ) extends ErrorHasUnderlay(underlying)

  final case class KinesisError(
    underlying: Throwable,
    code: ErrorCode
  ) extends ErrorHasUnderlay(underlying)

  final case class KinesisFirehoseError(
    underlying: Throwable,
    code: ErrorCode
  ) extends ErrorHasUnderlay(underlying)
}
