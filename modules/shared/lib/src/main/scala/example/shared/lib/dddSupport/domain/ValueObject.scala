package example.shared.lib.dddSupport.domain

/**
  * 値オブジェクトを表すトレイト
  */
trait ValueObject extends Serializable {

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
