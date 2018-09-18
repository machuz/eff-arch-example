package example.shared.lib.auth

import example.shared.lib.dddSupport.domain.ValueObject

sealed abstract class CryptType(val value: Int) extends ValueObject {
  def messageDigestString: String
}

/**
  * パスワード暗号化の種類
  */
object CryptType {

  case object MD5 extends CryptType(1) {
    override def messageDigestString: String =
      "MD5"
  }

  case object SHA256 extends CryptType(2) {
    override def messageDigestString: String =
      "SHA-256"
  }

  def valueOf(value: Int): CryptType = {
    value match {
      case 1 => MD5
      case 2 => SHA256
      case _ => throw new IllegalArgumentException(s"CryptType[$value] is not supported")
    }
  }

  val values = Seq(MD5, SHA256)

  val default = SHA256

  def apply(value: Int): CryptType = valueOf(value)

}
