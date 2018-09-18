package example.shared.lib.dddSupport.domain

/**
  * RepositoryのIO例外
  * @param message エラーメッセージ
  */
case class RepositoryIOException(message: String) extends DomainException(message)
