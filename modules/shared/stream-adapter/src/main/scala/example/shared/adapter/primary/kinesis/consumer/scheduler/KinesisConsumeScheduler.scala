package example.shared.adapter.primary.kinesis.consumer.scheduler

import software.amazon.awssdk.auth.credentials.{ AwsBasicCredentials, StaticCredentialsProvider }
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.kinesis.common.ConfigsBuilder
import software.amazon.kinesis.coordinator.Scheduler

import java.net.InetAddress
import java.util.UUID

import scalaz.Tag

import jp.eigosapuri.es.shared.adapter.primary.kinesis.KinesisApplicationName
import jp.eigosapuri.es.shared.adapter.primary.kinesis.consumer.recordProcesser.{
  AbstractRecordProcesser,
  AbstractRecordProcesserFactory
}
import jp.eigosapuri.es.shared.adapter.secondary.eff.aws.{ AwsAccessKey, AwsRegion, AwsSecretAccessKey }
import jp.eigosapuri.es.shared.lib.logger.EsLogger

class KinesisConsumeScheduler[A <: AbstractRecordProcesser, B <: AbstractRecordProcesserFactory[A]](
  protected val streamName: String,
  accessKeyId: AwsAccessKey,
  secretAccessKey: AwsSecretAccessKey,
  region: AwsRegion,
  recordProcesserFactory: B
) {

  private val applicationName = KinesisApplicationName(streamName + "-consumer")
  private val credential      = AwsBasicCredentials.create(Tag.unwrap(accessKeyId), Tag.unwrap(secretAccessKey))
  private val cProvider       = StaticCredentialsProvider.create(credential)
  private val workerId        = applicationName + ":" + InetAddress.getLocalHost.getCanonicalHostName + ":" + UUID.randomUUID

  private val dynamoClient: DynamoDbAsyncClient =
    DynamoDbAsyncClient.builder
      .credentialsProvider(cProvider)
      .region(region.toSdkValue)
      .build

  private val cloudWatchClient: CloudWatchAsyncClient =
    CloudWatchAsyncClient.builder
      .credentialsProvider(cProvider)
      .region(region.toSdkValue)
      .build

  private val kinesisClient: KinesisAsyncClient =
    KinesisAsyncClient.builder
      .credentialsProvider(cProvider)
      .region(region.toSdkValue)
      .build

  private val configsBuilder = new ConfigsBuilder(
    streamName,
    Tag.unwrap(applicationName),
    kinesisClient,
    dynamoClient,
    cloudWatchClient,
    workerId,
    recordProcesserFactory
  ).tableName("hoge")

  private val scheduler = new Scheduler(
    configsBuilder.checkpointConfig,
    configsBuilder.coordinatorConfig,
    configsBuilder.leaseManagementConfig,
    configsBuilder.lifecycleConfig,
    configsBuilder.metricsConfig,
    configsBuilder.processorConfig,
    configsBuilder.retrievalConfig
  )

  def run = {

    scala.sys.addShutdownHook {
      scheduler.shutdown()
      println("kinesis scheduler shutdown.")
      Thread.sleep(1000)
    }

    try {
      scheduler.run()
    } catch {
      case e: Throwable if scheduler.gracefuleShutdownStarted() =>
        EsLogger.error("a", Map("hoge" -> s"sxxxxxxx ${e.getMessage}"))
        ()
      case e: Throwable =>
        EsLogger.error("a", Map("hoge" -> s"sxxxxxxx ${e.getMessage}"))
        scheduler.startGracefulShutdown()
        ()
    }
  }

  def listShareds = {
    scheduler.shardDetector().listShards()
  }

}
