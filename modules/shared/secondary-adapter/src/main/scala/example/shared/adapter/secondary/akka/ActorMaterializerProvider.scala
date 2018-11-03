package example.shared.adapter.secondary.akka
import com.google.inject.{ Inject, Provider }

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import example.shared.adapter.config.SharedAdapterConf

class ActorMaterializerProvider @Inject()(
  implicit actorSystem: ActorSystem
) extends Provider[ActorMaterializer] {
  val actorSystemName: String = SharedAdapterConf.Akka.actorSystemName

  override def get(): ActorMaterializer = ActorMaterializer(
    materializerSettings = None,
    namePrefix = Option(actorSystemName)
  )
}
