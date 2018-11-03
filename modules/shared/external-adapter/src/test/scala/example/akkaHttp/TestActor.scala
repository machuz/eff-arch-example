package example.akkaHttp

class TestActor extends AbstractAkkaHttpActor {

  override def preStart(): Unit = log.info("starting hello actor.")

  override def postStop(): Unit = log.info("stopping hello actor.")

  // HelloCommandを受け取ったらHelloReplyを返す
  override def receive: Receive = {
    case TestActor.HelloCommand(s) =>
      sender() ! TestActor.HelloReply(s"Hello $s!!")
  }
}

object TestActor {

  val name: String = "test"

  // 受信用メッセージ
  sealed trait ActorCommand
  final case class HelloCommand(name: String) extends ActorCommand

  // 返信用メッセージ
  sealed trait ActorReply
  final case class HelloReply(message: String) extends ActorReply

}
