package example.shared.lib.dddSupport

sealed trait Error                                                         extends Throwable
sealed class ErrorHasUnderlay(underlay: Throwable)                         extends Error
sealed class ErrorHasCode(code: ErrorCode)                                 extends Error
sealed class ErrorHasUnderlayAndCode(underlay: Throwable, code: ErrorCode) extends Error

object ErrorHasUnderlay

object Error {
  import ErrorCode._
  final case object Generic                      extends Error
  final case class ExternalApiError(msg: String) extends Error
  final case class InternalApiError(msg: String) extends Error

  final case class NonFatalError(
    underlying: Throwable,
    code: ErrorCode
  ) extends ErrorHasUnderlay(underlying)

  final case class HttpError(msg: String) extends Error

  final case class DatabaseError(
    underlying: Throwable,
    code: ErrorCode = SERVER_ERROR
  ) extends ErrorHasUnderlay(underlying)

  final case class DomainServiceError(code: ErrorCode) extends ErrorHasCode(code)
  final case class UseCaseError(code: ErrorCode)       extends ErrorHasCode(code)
  final case class RepositoryError(code: ErrorCode)    extends ErrorHasCode(code)

  final case class RedisError(code: ErrorCode = REDIS_COMMAND_ERROR) extends Error

  final case class ThirdPartyServiceError(
    underlying: Throwable,
    code: ErrorCode
  ) extends ErrorHasUnderlayAndCode(underlying, code)

  final case class KinesisError(
    underlying: Throwable,
    code: ErrorCode
  ) extends ErrorHasUnderlayAndCode(underlying, code)

  final case class KinesisFirehoseError(
    underlying: Throwable,
    code: ErrorCode
  ) extends ErrorHasUnderlayAndCode(underlying, code)

  final case class CirceError(
    underlying: Throwable,
    code: ErrorCode
  ) extends ErrorHasUnderlayAndCode(underlying, code)

  final case class FormValidationError(code: ErrorCode, errors: List[FieldError]) extends ErrorHasCode(code)
  final case class FieldError(code: ErrorCode, attribute: String, message: String) extends ErrorHasCode(code)

}
