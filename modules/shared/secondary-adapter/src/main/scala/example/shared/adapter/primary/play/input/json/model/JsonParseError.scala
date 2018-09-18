package example.shared.adapter.primary.play.input.json.model

import play.api.libs.json.{ JsPath, JsonValidationError }

/**
  * Jsonパースエラー情報
  * ※パースエラーはサーバーで組み立てるレスポンスでは起きない前提でRequestのみ用意
  * @param items パースエラーがおきたPathと内容
  */
case class JsonParseError(
  items: Seq[JsonParseErrorItem]
) {
  override def toString: String = {
    items
      .map { i =>
        s"""path:[${i.path.toString}] msg:[${i.errors.map(x => x.message).mkString(",")}]"""
      }
      .zipWithIndex
      .toString
  }
}

object JsonParseError {
  def apply(
    errors: Seq[(JsPath, Seq[JsonValidationError])]
  )(implicit e: scala.reflect.ClassTag[Seq[(JsPath, Seq[JsonValidationError])]]): JsonParseError = {
    val items: Seq[JsonParseErrorItem] = errors.map {
      case (jsPath, validationErrors) =>
        JsonParseErrorItem(jsPath, validationErrors)
    }
    JsonParseError(items)
  }
}

case class JsonParseErrorItem(
  path: JsPath,
  errors: Seq[JsonValidationError]
)
