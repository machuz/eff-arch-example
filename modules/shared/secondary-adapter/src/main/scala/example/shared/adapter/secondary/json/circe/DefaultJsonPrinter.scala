package example.shared.adapter.secondary.json.circe

import io.circe.Encoder
import io.circe.syntax._

class DefaultJsonPrinter extends JsonPrinter {
  override def print[A](obj: A)(implicit encoder: Encoder[A]): String = obj.asJson.noSpaces
}
