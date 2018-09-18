package example.shared.lib.dddSupport.domain

import com.eaio.uuid.UUID

/**
  * ID生成器
  */
trait UUIDIdGenerator {

  def generate: UUID
}

class UUIDIdGeneratorImpl extends UUIDIdGenerator {

  // TODO: スケールしていったらsnowflakeなども検討する
  override def generate: UUID = new UUID()

}
