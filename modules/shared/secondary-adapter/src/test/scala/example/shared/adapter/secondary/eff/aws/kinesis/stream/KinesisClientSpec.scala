package jp.eigosapuri.es.shared.adapter.secondary.eff.aws.kinesis.stream

import com.amazonaws.services.kinesis.AmazonKinesis
import com.amazonaws.services.kinesis.model.{
  PutRecordRequest,
  PutRecordResult,
  PutRecordsRequest,
  PutRecordsRequestEntry,
  PutRecordsResult
}

import org.mockito.Mockito

import scala.collection.JavaConverters._

import scalaz.{ -\/, \/-, Tag }

import jp.eigosapuri.es.shared.adapter.secondary.eff.aws.kinesis.{ DeliveryStreamName, StreamName }
import jp.eigosapuri.es.shared.adapter.secondary.eff.aws.{ AwsAccessKey, AwsRegion, AwsSecretAccessKey }
import jp.eigosapuri.es.shared.lib.dddSupport.ErrorCode
import jp.eigosapuri.es.shared.lib.dddSupport.EsError.{ KinesisError, ThirdPartyServiceError }
import jp.eigosapuri.es.shared.lib.test.{ AbstractSpecification, DeterministicTestObject }

class KinesisClientSpec extends AbstractSpecification {
  import Mockito._
  import monix.execution.Scheduler.Implicits.global

  trait SetUp {
    val (_, (testItem1, testItem2, testRes, testBatchRes)) = (for {
      testItem1    <- DeterministicTestObject[KinesisItem]
      testItem2    <- DeterministicTestObject[KinesisItem]
      testRes      <- DeterministicTestObject[PutRecordResult]
      testBatchRes <- DeterministicTestObject[PutRecordsResult]
    } yield {
      (
        testItem1,
        testItem2,
        testRes,
        testBatchRes
      )
    }).apply(0)

    val streamName: StreamName = StreamName("test-stream")
    val req = new PutRecordRequest()
      .withStreamName(Tag.unwrap(streamName))
      .withPartitionKey(testItem1.partitionKey)
      .withData(testItem1.toRecord.getData)

    val items = Seq(testItem1, testItem2)

    val batchReqEntry = new PutRecordsRequestEntry()
      .withPartitionKey(testItem1.partitionKey)
      .withData(testItem1.toRecord.getData)

    val batchReq = new PutRecordsRequest()
      .withStreamName(Tag.unwrap(streamName))
      .withRecords(Seq(batchReqEntry).asJavaCollection)

    val kinesisClient = Mockito.mock(classOf[AmazonKinesis])
    val c = new KinesisClientImpl(
      AwsAccessKey("a"),
      AwsSecretAccessKey("b"),
      AwsRegion.TOKYO
    ) {
      override protected val c: AmazonKinesis = kinesisClient
      override protected def createPutRecordRequest(
        streamName: StreamName,
        item: KinesisItem
      ): PutRecordRequest = req
      override protected def createPutRecordBatchRequest(
        streamName: StreamName,
        items: Seq[KinesisItem]
      ): PutRecordsRequest = batchReq
    }

    val testException = new RuntimeException("kineis error")
  }

  "KinesisClient" should {

    "putRecord" must {
      "be successful" in new SetUp {
        when(kinesisClient.putRecord(req))
          .thenReturn(testRes)

        val actual =
          c.putRecord(streamName, testItem1)

        val expected = \/-(())
        await(actual) must be(expected)
      }

      "be failed" in new SetUp {
        when(kinesisClient.putRecord(req))
          .thenThrow(testException)

        val actual =
          c.putRecord(streamName, testItem1)

        val expected = -\/(KinesisError(testException, ErrorCode.AWS_API_ERROR))
        await(actual) must be(expected)
      }
    }

    "putRecordBatch" must {
      "be successful" in new SetUp {
        when(kinesisClient.putRecords(batchReq))
          .thenReturn(testBatchRes)

        val actual =
          c.putRecordBatch(streamName, items)

        val expected = \/-(())
        await(actual) must be(expected)
      }

      "be failed" in new SetUp {
        when(kinesisClient.putRecords(batchReq))
          .thenThrow(testException)

        val actual =
          c.putRecordBatch(streamName, items)

        val expected = -\/(KinesisError(testException, ErrorCode.AWS_API_ERROR))
        await(actual) must be(expected)
      }
    }
  }

}
