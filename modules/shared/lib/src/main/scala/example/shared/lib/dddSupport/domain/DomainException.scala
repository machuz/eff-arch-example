package example.shared.lib.dddSupport.domain

/**
  * Domain層の例外
  * @param message error msg
  */
abstract class DomainException(message: String) extends Exception(message)
