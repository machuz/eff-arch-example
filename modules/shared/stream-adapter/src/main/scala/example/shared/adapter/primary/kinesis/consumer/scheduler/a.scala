package example.shared.adapter.primary.kinesis.consumer.scheduler

import software.amazon.kinesis.lifecycle.events.{ LeaseLostInput, ProcessRecordsInput }

import scala.collection.JavaConverters._

import jp.eigosapuri.es.shared.adapter.primary.kinesis.consumer.AbstractKinesisConsumer
import jp.eigosapuri.es.shared.adapter.primary.kinesis.consumer.recordProcesser.{
  AbstractRecordProcesser,
  AbstractRecordProcesserFactory
}
import jp.eigosapuri.es.shared.adapter.secondary.eff.aws.{ AwsAccessKey, AwsRegion, AwsSecretAccessKey }

class TestConsumer(
  streamName: String,
  accessKeyId: AwsAccessKey,
  secretAccessKey: AwsSecretAccessKey,
  region: AwsRegion,
  recordProcesserFactory: TestRecordProcesserFactory
) extends AbstractKinesisConsumer[
    TestRecordProcesser,
    TestRecordProcesserFactory
  ](
    streamName,
    accessKeyId,
    secretAccessKey,
    region,
    recordProcesserFactory
  ) {}

class TestRecordProcesserFactory extends AbstractRecordProcesserFactory[TestRecordProcesser] {
  override def shardRecordProcessor(): TestRecordProcesser = {
    new TestRecordProcesser
  }
}

class TestRecordProcesser extends AbstractRecordProcesser {
  override def processRecords(processRecordsInput: ProcessRecordsInput): Unit = {
    println(processRecordsInput.records().asScala.map(_.data().toString))
  }

  override def leaseLost(leaseLostInput: LeaseLostInput): Unit = {
    println(leaseLostInput.toString)
  }
}

//
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//
//import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
//import software.amazon.awssdk.regions.Region
//import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
//import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
//import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
//import software.amazon.kinesis.common.ConfigsBuilder
//import software.amazon.kinesis.coordinator.Scheduler
//
//import java.io.BufferedReader
//import java.io.IOException
//import java.net.InetAddress
//import java.util.UUID
//import java.util.concurrent.ExecutionException
//import java.util.concurrent.TimeUnit
//
//object AmazonKinesisRecordConsumerSample {
//  /*
//   * Before running the code: Fill in your AWS access credentials in the
//   * provided credentials file template, and be sure to move the file to the
//   * default location (~/.aws/credentials) where the sample code will load the
//   * credentials from.
//   * https://console.aws.amazon.com/iam/home?#security_credential
//   *
//   * WARNING: To avoid accidental leakage of your credentials, DO NOT keep the
//   * credentials file in your source directory.
//   */
//  val SAMPLE_APPLICATION_STREAM_NAME  = "2.xTest"
//  private val SAMPLE_APPLICATION_NAME = "2.xApplication"
//  private val REGION                  = Region.US_WEST_2
//  private val log                     = LoggerFactory.getLogger(classOf[AmazonKinesisRecordConsumerSample])
//  private var credentialsProvider     = null
//
//  @throws[Exception]
//  private def init(): Unit = { // Ensure the JVM will refresh the cached IP values of AWS resources
//    // (e.g. service endpoints).
//    java.security.Security.setProperty("networkaddress.cache.ttl", "60")
//    /*
//     * The ProfileCredentialsProvider will return your [default] credential
//     * profile by reading from the credentials file located at
//     * (~/.aws/credentials).
//     */
//    credentialsProvider = DefaultCredentialsProvider.create
//    try credentialsProvider.resolveCredentials
//    catch {
//      case e: Exception =>
//        throw new Exception(
//          "Cannot load the credentials from the credential profiles file. " + "Please make sure that your credentials file is at the correct " + "location (~/.aws/credentials), and is in valid format.",
//          e
//        )
//    }
//  }
//
//  @throws[Exception]
//  def main(args: Array[String]): Unit = {
//    init()
//    val workerId         = InetAddress.getLocalHost.getCanonicalHostName + ":" + UUID.randomUUID
//    val dynamoClient     = DynamoDbAsyncClient.builder.region(REGION).build
//    val cloudWatchClient = CloudWatchAsyncClient.builder.region(REGION).build
//    val kinesisClient    = KinesisAsyncClient.builder.region(REGION).credentialsProvider(credentialsProvider).build
//    val configsBuilder = new ConfigsBuilder(
//      SAMPLE_APPLICATION_STREAM_NAME,
//      SAMPLE_APPLICATION_NAME,
//      kinesisClient,
//      dynamoClient,
//      cloudWatchClient,
//      workerId,
//      new Nothing
//    )
//    val scheduler = new Scheduler(
//      configsBuilder.checkpointConfig,
//      configsBuilder.coordinatorConfig,
//      configsBuilder.leaseManagementConfig,
//      configsBuilder.lifecycleConfig,
//      configsBuilder.metricsConfig,
//      configsBuilder.processorConfig,
//      configsBuilder.retrievalConfig
//    )
//    val schedulerThread = new Thread(scheduler)
//    schedulerThread.setDaemon(true)
//    schedulerThread.start()
//    System.out.println("Press enter to shutdown")
//    val reader = new BufferedReader(new Nothing(System.in))
//    try reader.readLine
//    catch {
//      case ioex: IOException =>
//        log.error("Caught exception while waiting for confirm.  Shutting down", ioex)
//    }
//    val gracefulShutdownFuture = scheduler.startGracefulShutdown
//    log.info("Waiting up to 20 seconds for shutdown to complete.")
//    try gracefulShutdownFuture.get(20, TimeUnit.SECONDS)
//    catch {
//      case e: InterruptedException =>
//        log.info("Interrupted while waiting for graceful shutdown. Continuing.")
//      case e: ExecutionException =>
//        log.error("Exception while executing graceful shutdown.", e)
//      case e: Nothing =>
//        log.error("Timeout while waiting for shutdown.  Scheduler may not have exited.")
//    }
//    log.info("Completed, shutting down now.")
//  }
//}
