package example.shared.lib.dddSupport.domain.cache

import org.scalatest.prop.GeneratorDrivenPropertyChecks

import example.shared.lib.test.AbstractSpecification
import io.circe._

import scalaz.\/-

import jp.eigosapuri.es.shared.lib.dddSupport.domain.Identifier

class CacheableIdSpec extends AbstractSpecification with GeneratorDrivenPropertyChecks {
  import CacheableIdSpec._

  "cacheableId" should {
    "encode" in forAll { (s: String) =>
      val testId   = TestId(s)
      val expected = Json.fromString(s).noSpaces
      val actual   = TestId.encode(testId)

      actual must be(expected)
    }

    "decode" in forAll { (s: String) =>
      val jsonStr  = Json.fromString(s).noSpaces
      val expected = TestId(s)
      val actual   = TestId.decode(jsonStr)

      actual must be(\/-(expected))
    }

  }
}

object CacheableIdSpec {
  case class TestId(value: String) extends Identifier[String]
  object TestId extends CacheableId[TestId] {
    override implicit val encoder: Encoder[TestId] = deriveIdentifierEncoder
    override implicit val decoder: Decoder[TestId] = deriveIdentifierDecoder
  }
}
