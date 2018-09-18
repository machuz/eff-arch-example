package example.shared.adapter.primary.kinesis.consumer.recordProcesser

import software.amazon.kinesis.processor.ShardRecordProcessorFactory

abstract class AbstractRecordProcesserFactory[A <: AbstractRecordProcesser] extends ShardRecordProcessorFactory {

  override def shardRecordProcessor(): A
}
