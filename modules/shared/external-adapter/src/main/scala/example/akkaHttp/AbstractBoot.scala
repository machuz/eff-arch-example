package example.akkaHttp
import akka.actor.ActorSystem
import javax.inject.Inject

class AbstractBoot @Inject()(
  actorSystem: ActorSystem
) {

  def main(args: Array[String]): Unit = {
    println("hello")
  }
}
