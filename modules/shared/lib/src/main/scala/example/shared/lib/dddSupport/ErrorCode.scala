package example.shared.lib.dddSupport

/**
  * エラーコード
  */
sealed class ErrorCode(value: Int)

object ErrorCode {
  case object INVALID_REQUEST     extends ErrorCode(400000)
  case object INVALID_PARAMETERS  extends ErrorCode(400100)
  case object UNAUTHORIZED        extends ErrorCode(401000)
  case object ACCESS_FORBIDDEN    extends ErrorCode(403000)
  case object RESOURCE_NOT_FOUND  extends ErrorCode(404000)
  case object CONFLICT            extends ErrorCode(409000)
  case object SERVER_ERROR        extends ErrorCode(500000)
  case object EXTERNAL_API_ERROR  extends ErrorCode(500104)
  case object JSON_PARSE_ERROR    extends ErrorCode(500105)
  case object HTTP_REQUEST_ERROR  extends ErrorCode(500106)
  case object REDIS_REQUEST_ERROR extends ErrorCode(500107)
  case object REDIS_COMMAND_ERROR extends ErrorCode(500108)
  case object AWS_API_ERROR       extends ErrorCode(500109)

}
