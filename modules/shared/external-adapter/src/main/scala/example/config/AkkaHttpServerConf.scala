package example.config

import example.shared.adapter.config.support.ApplicationConfBase

class AkkaHttpServerConf extends ApplicationConfBase {
  def host: String = getString("akka.http.host")
  def port: Int    = getInt("akka.http.port")
}
