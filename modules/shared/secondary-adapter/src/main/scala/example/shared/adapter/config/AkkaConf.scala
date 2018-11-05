package example.shared.adapter.config
import example.shared.adapter.config.support.ApplicationConfBase

class AkkaConf extends ApplicationConfBase {

  lazy val actorSystemName: String = getStringOpt("akka.actorSystem.name").getOrElse("default")

}
