package example.shared.lib.dddSupport.domain.cache

import org.scalatest.prop.GeneratorDrivenPropertyChecks

import example.shared.lib.test.AbstractSpecification
import io.circe._

import scalaz.\/-

import jp.eigosapuri.es.shared.lib.dddSupport.domain.ValueObject

class CacheableValueObjectSpec extends AbstractSpecification with GeneratorDrivenPropertyChecks {
  import CacheableValueObjectSpec._

  "CacheableValueObject" should {
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

object CacheableValueObjectSpec {
  case class TestVO(value: String) extends ValueObject
  object TestVO extends CacheableValueObject[TestVO] {
    override implicit val encoder: Encoder[TestVO] = deriveValueObjectEncoder
    override implicit val decoder: Decoder[TestVO] = deriveValueObjectDecoder
  }
}
