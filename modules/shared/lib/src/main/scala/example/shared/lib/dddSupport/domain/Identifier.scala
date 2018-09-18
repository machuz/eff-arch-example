package example.shared.lib.dddSupport.domain

/**
  * Entityの識別子を表すトレイト
  *
  * @tparam A 識別子の値型
  */
trait Identifier[+A] {

  def value: A

  /**
    * ハッシュコードを返す
    *
    * @return HashCode
    */
  def hashCode: Int

  /**
    * 指定されたオブジェクトと等価であるかを判定する
    *
    * @param that 値オブジェクト
    * @return true 等価である場合
    *         false 等価でない場合
    */
  def equals(that: Any): Boolean
}

trait IdGenerator[A <: Identifier[String]] {
  def generate(value: String): A
}
