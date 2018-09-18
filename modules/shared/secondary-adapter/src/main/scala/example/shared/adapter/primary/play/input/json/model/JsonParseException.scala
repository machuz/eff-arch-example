package example.shared.adapter.primary.play.input.json.model

/**
  * JsonParse時の例外
  *
  * @param message エラーメッセージ
  */
class JsonParseException(val message: String) extends Exception(message)
