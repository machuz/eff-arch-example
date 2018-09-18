package example.shared.lib.eff.util.idGen

import com.eaio.uuid.UUID

import org.atnos.eff.Eff
import org.atnos.eff.syntax.eff._
import org.mockito.{ Matchers, Mockito }

import example.shared.lib.test.AbstractSpecification

import jp.eigosapuri.es.shared.lib.dddSupport.domain.{ IdGenerator, Identifier, UUIDIdGenerator }
import jp.eigosapuri.es.shared.lib.eff.util.idGen.IdGenInterpretationTypes.IdGenStack
import jp.eigosapuri.es.shared.lib.eff.util.idGen.IdGenTypes._idgen
import jp.eigosapuri.es.shared.lib.eff.util.idGen.interpreter.{ IdGenInterpreter, IdGenInterpreterImpl }

class IdGenEffectSpec extends AbstractSpecification {

  import IdGenEffect._
  import IdGenEffectSpec._
  import Matchers._
  import Mockito._

  trait SetUp {
    val uuid                        = new UUID()
    implicit val g: UUIDIdGenerator = Mockito.mock(classOf[UUIDIdGenerator])
    implicit val interpreter        = new IdGenInterpreterImpl(g)
    type R = IdGenStack
  }

  "IdGenEffect" should {

    "`generate` be successful" in new SetUp {

      when(g.generate).thenReturn(uuid)

      val actual =
        (for {
          id <- TestId.generateEff[R]
        } yield id).runIdGen.runPure

      actual must be(Some(TestId(uuid.toString)))
    }
  }

}

object IdGenEffectSpec {

  case class TestId(value: String) extends Identifier[String]
  object TestId {

    def generateEff[R: _idgen]: Eff[R, TestId] = {
      val gen = new IdGenerator[TestId] {
        override def generate(value: String): TestId =
          TestId(value)
      }
      IdGenEffect.generate[TestId, R](gen)
    }
  }

}
