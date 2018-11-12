package example.shared.adapter.config.di
import com.google.inject.AbstractModule

import example.shared.adapter.secondary.json.circe.{ DefaultJsonPrinter, JsonPrinter }

class JsonModule extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[JsonPrinter]).to(classOf[DefaultJsonPrinter])
  }

}
