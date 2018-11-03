package example.akkaHttp

class TestActor extends AbstractAkkaHttpActor {

  override def preStart(): Unit = log.info("starting hello actor.")

  override def postStop(): Unit = log.info("stopping hello actor.")

  // HelloCommandを受け取ったらHelloReplyを返す
  override def receive: Receive = {
    case TestActor.HelloCommand(name) =>
      sender() ! TestActor.HelloReply(s"Hello $name!!")
  }
}

object TestActor {

  // 受信用メッセージ
  final case class HelloCommand(name: String)

  // 返信用メッセージ
  final case class HelloReply(message: String)

}
