package example.akkaHttp

import io.circe.Json

trait OutputJson { self =>
  def toJson: Json
}
