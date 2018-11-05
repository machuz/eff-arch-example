package example.akkaHttp

import example.shared.adapter.config.support.ApplicationConfBase

class AkkaHttpServerConf extends ApplicationConfBase {
  val host: String = getString("akka.http.host")
  val port: Int    = getInt("akka.http.port")
}
