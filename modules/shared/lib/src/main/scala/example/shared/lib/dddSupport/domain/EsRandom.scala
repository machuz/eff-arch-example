package example.shared.lib.dddSupport.domain

import java.security.SecureRandom

import scala.util.Random

trait EsRandom {

  val random: Random
  def generateRandomString(length: Int): String
}

class EsRandomImpl extends EsRandom {

  val random: Random = new Random()

  def generateRandomString(length: Int): String = random.alphanumeric.take(length).mkString
}

class EsSecureRandomImpl extends EsRandom {
  // /dev/randomを用いたセキュア実装。
  // SecureRandomはブロックする可能性があるが、本番環境では問題にならないと考えられる。
  // もし、絶対にスレッドが詰ってはならない場合は、疑似乱数による実装を検討する。
  val random: Random = new Random(new SecureRandom)

  def generateRandomString(length: Int): String = random.alphanumeric.take(length).mkString
}
