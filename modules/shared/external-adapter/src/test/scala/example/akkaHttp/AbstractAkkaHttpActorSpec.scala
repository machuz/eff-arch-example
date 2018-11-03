package example.akkaHttp

import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

import akka.actor.{ ActorSystem, Props }
import akka.testkit.{ TestKit, TestProbe }

class AbstractAkkaHttpActor
  extends TestKit(ActorSystem("test-actor-spec"))
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  import TestActor._

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  "hello actor" should {

    "hello!!" in {
      val probe      = TestProbe()
      val helloActor = system.actorOf(Props[AbstractAkkaHttpActor])

      helloActor.tell(HelloCommand("ME"), probe.ref)
      probe.expectMsg(HelloReply("Hello ME!!"))
    }
  }

}

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

