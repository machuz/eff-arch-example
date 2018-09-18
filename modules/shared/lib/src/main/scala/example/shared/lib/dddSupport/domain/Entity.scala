package example.shared.lib.dddSupport.domain

/**
  * エンティティの責務を表すトレイト
  *
  * @tparam ID 識別子
  */
trait Entity[ID <: Identifier[_]] {

  val identifier: ID

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
