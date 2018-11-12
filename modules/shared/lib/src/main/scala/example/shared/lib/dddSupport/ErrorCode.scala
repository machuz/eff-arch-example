package example.shared.lib.dddSupport

/**
  * エラーコード
  */
sealed class ErrorCode(val value: String)

object ErrorCode {
  case object AKKA_HTTP_REJECTION_HANDLE_ERROR extends ErrorCode("AKKA_HTTP_REJECTION_HANDLE_ERROR")
  case object INVALID_REQUEST                  extends ErrorCode("INVALID_REQUEST")

  case object INVALID_PARAMETERS extends ErrorCode("INVALID_PARAMETERS")

  case object UNAUTHORIZED extends ErrorCode("UNAUTHORIZED")

  case object FORBIDDEN extends ErrorCode("FORBIDDEN")

  case object RESOURCE_NOT_FOUND extends ErrorCode("RESOURCE_NOT_FOUND")

  case object CONFLICT extends ErrorCode("CONFLICT")

  case object SERVER_ERROR        extends ErrorCode("SERVER_ERROR")
  case object JSON_PARSE_ERROR    extends ErrorCode("JSON_PARSE_ERROR")
  case object HTTP_REQUEST_ERROR  extends ErrorCode("HTTP_REQUEST_ERROR")
  case object REDIS_REQUEST_ERROR extends ErrorCode("REDIS_REQUEST_ERROR")
  case object REDIS_COMMAND_ERROR extends ErrorCode("REDIS_COMMAND_ERROR")
  case object AWS_API_ERROR       extends ErrorCode("AWS_API_ERROR")

}
