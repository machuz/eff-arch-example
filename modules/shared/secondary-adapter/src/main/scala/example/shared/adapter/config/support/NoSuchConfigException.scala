package example.shared.adapter.config.support

/**
  * 必須のコンフィグがなかった場合の例外
  * @param message エラーメッセージ
  */
class NoSuchConfigException(val message: String) extends Exception(message)
