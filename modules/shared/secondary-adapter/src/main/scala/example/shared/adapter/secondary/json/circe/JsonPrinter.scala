package example.shared.adapter.secondary.json.circe

import io.circe.Encoder

abstract class JsonPrinter {
  def print[A](obj: A)(implicit encoder: Encoder[A]): String
}
