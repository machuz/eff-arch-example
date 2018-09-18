package example.shared.lib.util

import example.shared.lib.auth.CryptType
import example.shared.lib.auth.CryptType.{ MD5, SHA256 }

object PasswordUtils {

  private val stretchCount: Int = 10

  private def getHash(target: String, cryptType: CryptType): String =
    java.security.MessageDigest
      .getInstance(cryptType.messageDigestString)
      .digest(target.getBytes)
      .map("%02x".format(_))
      .mkString

  def encrypt(target: String, salt: String, cryptType: CryptType): String =
    cryptType match {
      case MD5 =>
        getHash(target, cryptType)
      case SHA256 =>
        (1 to stretchCount).foldLeft("") { (acc, _) =>
          getHash(acc + salt + target, cryptType)
        }
    }
}
