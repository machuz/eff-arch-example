package example.shared.lib.dddSupport.domain.cache

import org.scalatest.prop.GeneratorDrivenPropertyChecks

import example.shared.lib.test.AbstractSpecification
import io.circe._

import scalaz.\/-

import jp.eigosapuri.es.shared.lib.dddSupport.domain.ValueObject
import jp.eigosapuri.es.shared.lib.logger.EsLogger

class CacheableAdtValueObjectSpec extends AbstractSpecification with GeneratorDrivenPropertyChecks {
  import CacheableAdtValueObjectSpec._

  "CacheableAdtValueObject" should {
    "encode" in forAll { (s: String) =>
      val vo       = TestVO(s)
      val expected = Json.fromString(s).noSpaces
      val actual   = TestVO.encode(vo)

      actual must be(expected)
    }

    "decode" in forAll { (s: String) =>
      val jsonStr  = Json.fromString(s).noSpaces
      val expected = TestVO(s)
      val actual   = TestVO.decode(jsonStr)

      actual must be(\/-(expected))
    }
  }

}

object CacheableAdtValueObjectSpec {
  sealed abstract class TestVO(val value: String) extends ValueObject
  object TestVO extends CacheableAdtValueObject[TestVO] {
    case object TestVO1                            extends TestVO("test1")
    case object TestVO2                            extends TestVO("test2")
    case class Unknown(override val value: String) extends TestVO(value)
    def valueOf(value: String): TestVO = {
      value match {
        case "test1" => TestVO1
        case "test2" => TestVO2
        case n =>
          EsLogger.error("TestVO", Map("msg" -> s"$n"))
          Unknown(n)
      }
    }
    def apply(value: String): TestVO = valueOf(value)

    override implicit val encoder: Encoder[TestVO] = Encoder.instance(x => Json.fromString(x.value))
    override implicit val decoder: Decoder[TestVO] = Decoder[String].map(x => TestVO(x))
  }
}
