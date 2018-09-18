package example.shared.adapter.primary.kinesis.consumer.recordProcesser

import software.amazon.kinesis.lifecycle.events.{
  InitializationInput,
  LeaseLostInput,
  ProcessRecordsInput,
  ShardEndedInput,
  ShutdownRequestedInput
}
import software.amazon.kinesis.processor.ShardRecordProcessor

import jp.eigosapuri.es.shared.lib.logger.EsLogger

abstract class AbstractRecordProcesser extends ShardRecordProcessor {
  override def initialize(initializationInput: InitializationInput): Unit = {
    EsLogger.info(
      "KinesisProcesser",
      Map(
        "msg"                    -> s"Initialising record processor",
        "sharedId"               -> initializationInput.shardId,
        "extendedSequenceNumber" -> initializationInput.extendedSequenceNumber.sequenceNumber()
      )
    )
  }

  def processRecords(processRecordsInput: ProcessRecordsInput): Unit

  def leaseLost(leaseLostInput: LeaseLostInput): Unit

  override def shardEnded(shardEndedInput: ShardEndedInput): Unit = {
    import software.amazon.kinesis.exceptions.{ InvalidStateException, ShutdownException }
    try shardEndedInput.checkpointer.checkpoint()
    catch {
      case e @ (_: ShutdownException | _: InvalidStateException) =>
        e.printStackTrace()
    }
  }

  override def shutdownRequested(shutdownRequestedInput: ShutdownRequestedInput): Unit = {
    import software.amazon.kinesis.exceptions.{ InvalidStateException, ShutdownException }
    try shutdownRequestedInput.checkpointer.checkpoint()
    catch {
      case e @ (_: ShutdownException | _: InvalidStateException) =>
        e.printStackTrace()
    }
  }
}
