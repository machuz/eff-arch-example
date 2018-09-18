package example.shared.adapter.primary.kinesis.consumer

import software.amazon.awssdk.auth.credentials.{ AwsBasicCredentials, StaticCredentialsProvider }

import java.net.InetAddress
import java.util.UUID

import scalaz.Tag

import jp.eigosapuri.es.shared.adapter.primary.kinesis.consumer.recordProcesser.{
  AbstractRecordProcesser,
  AbstractRecordProcesserFactory
}
import jp.eigosapuri.es.shared.adapter.primary.kinesis.consumer.scheduler.KinesisConsumeScheduler
import jp.eigosapuri.es.shared.adapter.secondary.eff.aws.{ AwsAccessKey, AwsRegion, AwsSecretAccessKey }
import jp.eigosapuri.es.shared.lib.logger.EsLogger

abstract class AbstractKinesisConsumer[A <: AbstractRecordProcesser, B <: AbstractRecordProcesserFactory[A]](
  protected val streamName: String,
  accessKeyId: AwsAccessKey,
  secretAccessKey: AwsSecretAccessKey,
  region: AwsRegion,
  recordProcesserFactory: B
) {

  protected val applicationName: String = streamName + "-consumer"

  private val credential = AwsBasicCredentials.create(Tag.unwrap(accessKeyId), Tag.unwrap(secretAccessKey))
  private val cProvider  = StaticCredentialsProvider.create(credential)
  private val workerId   = applicationName + ":" + InetAddress.getLocalHost.getCanonicalHostName + ":" + UUID.randomUUID

  val s = new KinesisConsumeScheduler[A, B](
    streamName,
    accessKeyId,
    secretAccessKey,
    region,
    recordProcesserFactory
  )

  def getScheduler: KinesisConsumeScheduler[A, B] = s

//  scheduler.shardDetector().listShards()

  def run(): Unit = {
    s.run
//    import software.amazon.kinesis.leases.ShardInfo
//    import software.amazon.kinesis.retrieval.kpl.ExtendedSequenceNumber
////    val shardId          = "shardId-000000000000"
////    val concurrencyToken = "concurrencyToken"
////    val shardInfo        = new ShardInfo(shardId, concurrencyToken, null, ExtendedSequenceNumber.TRIM_HORIZON)
//    try {
//      scheduler.run()
//    } catch {
//      case e: Throwable if scheduler.gracefuleShutdownStarted() =>
//        EsLogger.error("a", Map("hoge" -> s"sxxxxxxx ${e.getMessage}"))
//      case e: Throwable =>
//        EsLogger.error("a", Map("hoge" -> s"sxxxxxxx ${e.getMessage}"))
//        scheduler.createGracefulShutdownCallable().call()
//    }
//    scheduler.createOrGetShardConsumer(shardInfo, recordProcesserFactory)

//    val schedulerThread: Thread = new Thread(scheduler)
//    schedulerThread.setDaemon(true)
//    schedulerThread.start()

//    println("Press enter to shutdown")
//    val reader = new BufferedReader(new InputStreamReader(System.in))
//    try {
//      reader.readLine
//    } catch {
//      case e: IOException =>
//        EsLogger.error(
//          "kinesis consumer",
//          Map(
//            "msg"        -> "Caught exception while waiting for confirm.  Shutting down",
//            "exception"  -> e.getMessage,
//            "stackTrace" -> e.getStackTrace.mkString(",")
//          )
//        )
//    }
//    val gracefulShutdownFuture = scheduler.startGracefulShutdown
//    EsLogger.info(
//      "kinesis consumer",
//      Map("msg" -> "Waiting up to 20 seconds for shutdown to complete.")
//    )
//
//    try {
//      gracefulShutdownFuture.get(20, TimeUnit.SECONDS)
//    } catch {
//      case _: InterruptedException =>
//        EsLogger.info(
//          "kinesis consumer",
//          Map("msg" -> "Interrupted while waiting for graceful shutdown. Continuing.")
//        )
//      case e: ExecutionException =>
//        EsLogger.info(
//          "kinesis consumer",
//          Map(
//            "msg"        -> "Exception while executing graceful shutdown.",
//            "exception"  -> e.getMessage,
//            "stackTrace" -> e.getStackTrace.mkString(",")
//          )
//        )
//      case e: Throwable =>
//        EsLogger.info(
//          "kinesis consumer",
//          Map(
//            "msg"        -> "Timeout while waiting for shutdown. Scheduler may not have exited.",
//            "exception"  -> e.getMessage,
//            "stackTrace" -> e.getStackTrace.mkString(",")
//          )
//        )
//    }

    EsLogger.info(
      "kinesis consumer",
      Map("msg" -> "Completed, shutting down now.")
    )
  }

}
