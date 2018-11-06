package example.shared.adapter.secondary.akka
import com.google.inject.{ Inject, Provider }

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import example.shared.adapter.config.AkkaConf

class ActorMaterializerProvider @Inject()(
  akkaConf: AkkaConf
)(
  implicit actorSystem: ActorSystem
) extends Provider[ActorMaterializer] {
  val actorSystemName: String = akkaConf.actorSystemName

  override def get(): ActorMaterializer = ActorMaterializer(
    materializerSettings = None,
    namePrefix = Option(actorSystemName)
  )
}
