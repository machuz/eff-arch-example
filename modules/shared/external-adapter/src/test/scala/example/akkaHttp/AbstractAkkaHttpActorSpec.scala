package example.akkaHttp

import org.scalatest.{ BeforeAndAfterAll, Matchers, WordSpecLike }

import akka.actor.{ ActorSystem, Props }
import akka.testkit.{ TestKit, TestProbe }

class AbstractAkkaHttpActorSpec
  extends TestKit(ActorSystem("test-actor-spec"))
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  import TestActor._

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  "hello actor" should {

    "hello!!" in {
      val probe     = TestProbe()
      val testActor = system.actorOf(Props[TestActor])

      testActor.tell(HelloCommand("ME"), probe.ref)
      probe.expectMsg(HelloReply("Hello ME!!"))
    }
  }

}
