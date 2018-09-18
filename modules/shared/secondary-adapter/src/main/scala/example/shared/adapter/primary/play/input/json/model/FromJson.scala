package example.shared.adapter.primary.play.input.json.model

import scalaz.Scalaz.ToEitherOps
import scalaz.\/

import play.api.libs.json.{ Format, JsValue, Json, Reads }
import play.api.libs.ws.JsonBodyReadables._
import play.api.libs.ws.JsonBodyWritables._

import jp.eigosapuri.es.shared.lib.logger.EsLogger

/**
  * RequestJsonからモデルへの変換
  */
trait FromJson[T] {

  // TODO: クライアントの対応が全て済んだら型に <: Requestの制約をつける
  def fromJson(json: JsValue)(implicit rds: Reads[T]): \/[JsonParseError, T] =
    Json
      .fromJson(json)(rds)
      .fold(
        valid = s => s.right,
        invalid = e => {

          val parseError = JsonParseError(e)
          EsLogger.error("jsonParseError", Map("errors" -> parseError.toString))
          parseError.left
        }
      )

}
