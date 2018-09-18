package example.shared.lib.dddSupport.domain.cache

import org.scalatest.prop.GeneratorDrivenPropertyChecks

import example.shared.lib.test.AbstractSpecification
import io.circe._

import scalaz.\/-

import jp.eigosapuri.es.shared.lib.dddSupport.domain.{ Identifier, ValueObject }

class CacheableModelSpec extends AbstractSpecification with GeneratorDrivenPropertyChecks {
  import CacheableModelSpec._

  "cacheableModel" should {

    "encode" in forAll { (id: String, v1: Int, v2: Option[String], v3: String) =>
      val testModel = TestModel(
        TestId(id),
        v1,
        v2,
        TestVO(v3)
      )
      val expected = TestModel.applyJsonStr(id, v1, v2, v3)
      val actual   = TestModel.encode(testModel)

      actual must be(expected)
    }

    "decode" in forAll { (id: String, v1: Int, v2: Option[String], v3: String) =>
      val jsonStr = TestModel.applyJsonStr(id, v1, v2, v3)
      val expected = TestModel(
        TestId(id),
        v1,
        v2,
        TestVO(v3)
      )

      val actual = TestModel.decode(jsonStr)

      actual must be(\/-(expected))
    }

  }
}

object CacheableModelSpec {
  case class TestId(value: String) extends Identifier[String]
  object TestId extends CacheableId[TestId] {
    override implicit val encoder: Encoder[TestId] = deriveIdentifierEncoder
    override implicit val decoder: Decoder[TestId] = deriveIdentifierDecoder
  }

  case class TestVO(value: String) extends ValueObject
  object TestVO extends CacheableValueObject[TestVO] {
    override implicit val encoder: Encoder[TestVO] = deriveValueObjectEncoder
    override implicit val decoder: Decoder[TestVO] = deriveValueObjectDecoder
  }

  case class TestModel(id: TestId, v1: Int, v2: Option[String], v3: TestVO)
  object TestModel extends CacheableModel[TestModel] {
    import io.circe.generic.semiauto._
    override implicit val encoder: Encoder[TestModel] = deriveEncoder
    override implicit val decoder: Decoder[TestModel] = deriveDecoder

    def applyJsonStr(id: String, v1: Int, v2: Option[String], v3: String): String = {
      Json
        .fromJsonObject(
          JsonObject.apply(
            ("id", Json.fromString(id)),
            ("v1", Json.fromInt(v1)),
            ("v2", v2.map(Json.fromString).getOrElse(Json.Null)),
            ("v3", Json.fromString(v3))
          )
        )
        .noSpaces
    }

  }

}
