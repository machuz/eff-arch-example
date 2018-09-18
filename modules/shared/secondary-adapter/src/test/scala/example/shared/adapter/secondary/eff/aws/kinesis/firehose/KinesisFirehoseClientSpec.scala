package jp.eigosapuri.es.shared.adapter.secondary.eff.aws.kinesis.firehose

import com.amazonaws.services.kinesisfirehose.AmazonKinesisFirehose
import com.amazonaws.services.kinesisfirehose.model.{
  PutRecordBatchRequest,
  PutRecordBatchResult,
  PutRecordRequest,
  PutRecordResult
}

import org.mockito.{ Matchers, Mockito }

import scala.collection.JavaConverters._

import scalaz.{ -\/, \/-, Tag }

import jp.eigosapuri.es.shared.adapter.secondary.eff.aws.kinesis.DeliveryStreamName
import jp.eigosapuri.es.shared.adapter.secondary.eff.aws.{ AwsAccessKey, AwsRegion, AwsSecretAccessKey }
import jp.eigosapuri.es.shared.lib.dddSupport.EsError.{ KinesisFirehoseError, ThirdPartyServiceError }
import jp.eigosapuri.es.shared.lib.dddSupport.{ ErrorCode, EsError }
import jp.eigosapuri.es.shared.lib.test.{ AbstractSpecification, DeterministicTestObject }

class KinesisFirehoseClientSpec extends AbstractSpecification {
  import Matchers._
  import Mockito._
  import monix.execution.Scheduler.Implicits.global

  trait SetUp {
    val (_, (testItem1, testItem2, testRes, testBatchRes)) = (for {
      testItem1    <- DeterministicTestObject[KinesisFirehoseItem]
      testItem2    <- DeterministicTestObject[KinesisFirehoseItem]
      testRes      <- DeterministicTestObject[PutRecordResult]
      testBatchRes <- DeterministicTestObject[PutRecordBatchResult]
    } yield {
      (
        testItem1,
        testItem2,
        testRes,
        testBatchRes
      )
    }).apply(0)

    val streamName: DeliveryStreamName = DeliveryStreamName("test-delivery-stream")
    val req = new PutRecordRequest()
      .withDeliveryStreamName(Tag.unwrap(streamName))
      .withRecord(testItem1.toRecord)

    val items = Seq(testItem1, testItem2)
    val batchReq = new PutRecordBatchRequest()
      .withDeliveryStreamName(Tag.unwrap(streamName))
      .withRecords(Seq(testItem1, testItem1).map(_.toRecord).asJavaCollection)

    val kinesisFirehoseClient = Mockito.mock(classOf[AmazonKinesisFirehose])
    val c = new KinesisFirehoseClientImpl(
      AwsAccessKey("a"),
      AwsSecretAccessKey("b"),
      AwsRegion.TOKYO
    ) {
      override protected val c: AmazonKinesisFirehose = kinesisFirehoseClient
      override protected def createPutRecordRequest(
        streamName: DeliveryStreamName,
        item: KinesisFirehoseItem
      ): PutRecordRequest = req
      override protected def createPutRecordBatchRequest(
        streamName: DeliveryStreamName,
        items: Seq[KinesisFirehoseItem]
      ): PutRecordBatchRequest = batchReq
    }

    val testException = new RuntimeException("kineis firehose error")
  }

  "KinesisFirehoseClient" should {

    "putRecord" must {
      "be successful" in new SetUp {
        when(kinesisFirehoseClient.putRecord(req))
          .thenReturn(testRes)

        val actual =
          c.putRecord(streamName, testItem1)

        val expected = \/-(())
        await(actual) must be(expected)
      }

      "be failed" in new SetUp {
        when(kinesisFirehoseClient.putRecord(req))
          .thenThrow(testException)

        val actual =
          c.putRecord(streamName, testItem1)

        val expected = -\/(KinesisFirehoseError(testException, ErrorCode.AWS_API_ERROR))
        await(actual) must be(expected)
      }
    }

    "putRecordBatch" must {
      "be successful" in new SetUp {
        when(kinesisFirehoseClient.putRecordBatch(batchReq))
          .thenReturn(testBatchRes)

        val actual =
          c.putRecordBatch(streamName, items)

        val expected = \/-(())
        await(actual) must be(expected)
      }

      "be failed" in new SetUp {
        when(kinesisFirehoseClient.putRecordBatch(batchReq))
          .thenThrow(testException)

        val actual =
          c.putRecordBatch(streamName, items)

        val expected = -\/(KinesisFirehoseError(testException, ErrorCode.AWS_API_ERROR))
        await(actual) must be(expected)
      }
    }
  }

}
