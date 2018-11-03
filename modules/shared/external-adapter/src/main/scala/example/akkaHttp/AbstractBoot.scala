package example.akkaHttp
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import javax.inject.Inject

class AbstractBoot @Inject()(
  implicit actorSystem: ActorSystem,
  actorMaterializer: ActorMaterializer
) {

  def main(args: Array[String]): Unit = {
    println("hello")
  }
}
