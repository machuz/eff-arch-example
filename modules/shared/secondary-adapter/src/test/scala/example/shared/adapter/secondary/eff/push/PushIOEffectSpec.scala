package jp.eigosapuri.es.shared.adapter.secondary.eff.push

import org.atnos.eff.syntax.addon.monix.task._
import org.atnos.eff.{ ExecutorServices, Fx, FxAppend }
import org.mockito.{ Matchers, Mockito }

import monix.eval.Task

import scala.concurrent.Future

import scalaz.Scalaz.none

import jp.eigosapuri.es.notificationApi.adapter.primary.grpc.interface.notification.push.{
  PublishPushNotificationRequest,
  PublishPushNotificationResponse,
  PublishSilentPushNotificationRequest
}
import jp.eigosapuri.es.notificationApi.adapter.primary.grpc.service.EsNotificationApiServiceGrpc.EsNotificationApiServiceStub
import jp.eigosapuri.es.shared.adapter.secondary.eff.push.CacheIOInterpretationTypes.PushIOStack
import jp.eigosapuri.es.shared.adapter.secondary.eff.push.interpreter.PushIOInterpreterImpl
import jp.eigosapuri.es.shared.domain.notification.push.Push
import jp.eigosapuri.es.shared.lib.eff.push.PushIO
import jp.eigosapuri.es.shared.lib.test.{ AbstractSpecification, DeterministicTestObject }

class PushIOEffectSpec extends AbstractSpecification {

  import Matchers._
  import Mockito._

  import monix.execution.Scheduler.Implicits.global

  import jp.eigosapuri.es.shared.adapter.secondary.eff.push.PushIOEffect._

  implicit val ec: ExecutorServices = org.atnos.eff.ExecutorServices.fromGlobalExecutionContext

  trait SetUp {
    val (_, (push, pReq, spReq)) = (for {
      push <- DeterministicTestObject[Push]
    } yield {
      (
        push,
        PublishPushNotificationRequest(
          accountId = push.accountId.value,
          accountType = push.accountId.accountType.value,
          title = push.title,
          subtitle = push.subtitle,
          message = push.message,
          pushType = none,
          priority = push.priority.value,
          badge = push.badge,
          extend = push.extend.flatMap(_.toMap).toMap
        ),
        PublishSilentPushNotificationRequest(
          accountId = push.accountId.value,
          accountType = push.accountId.accountType.value,
          message = push.message,
          priority = push.priority.value,
          badge = push.badge,
          extend = push.extend.flatMap(_.toMap).toMap
        )
      )
    }).apply(0)

    val c                    = Mockito.mock(classOf[EsNotificationApiServiceStub])
    implicit val interpreter = new PushIOInterpreterImpl(c)
    type R = FxAppend[Fx.fx1[Task], PushIOStack]
    val exception = new RuntimeException("test")
    val res       = PublishPushNotificationResponse()
  }

  "PushIOEffect" should {

    "Publish" must {

      "be successful" in new SetUp {
        when(c.publishPushNotification(pReq))
          .thenReturn(Future.successful(res))

        val actual   = PushIO.publish[R](push).runPushIO.runAsync.runAsync
        val expected = ()

        await(actual) must be(expected)
        verify(c, times(1)).publishPushNotification(any())
      }

      "be failed" in new SetUp {
        when(c.publishPushNotification(pReq))
          .thenThrow(exception)

        val actual   = PushIO.publish[R](push).runPushIO.runAsync.runAsync
        val expected = ()

        await(actual) must be(expected)
        verify(c, times(1)).publishPushNotification(any())
      }

    }

    "PublishSilent" must {

      "be successful" in new SetUp {
        when(c.publishSilentPushNotification(spReq))
          .thenReturn(Future.successful(res))

        val actual   = PushIO.publishSilent[R](push).runPushIO.runAsync.runAsync
        val expected = ()

        await(actual) must be(expected)
        verify(c, times(1)).publishSilentPushNotification(any())
      }

      "be failed" in new SetUp {
        when(c.publishSilentPushNotification(spReq))
          .thenThrow(exception)

        val actual   = PushIO.publishSilent[R](push).runPushIO.runAsync.runAsync
        val expected = ()

        await(actual) must be(expected)
        verify(c, times(1)).publishSilentPushNotification(any())
      }

    }

  }

}
