package example.shared.lib.eff.util.idGen

import com.eaio.uuid.UUID

import org.atnos.eff.Eff
import org.mockito.Mockito

import example.shared.lib.test.AbstractSpecification
import example.shared.lib.dddSupport.domain.{ IdGenerator, Identifier, UUIDIdGenerator }
import example.shared.lib.eff.IdGenStack
import example.shared.lib.eff.util.idGen.interpreter.IdGenInterpreterImpl
import example.shared.lib.eff.myEff._
import example.shared.lib.eff.atnosEffSyntax._

class IdGenEffectSpec extends AbstractSpecification {

  import IdGenEffectSpec._
  import org.mockito.Matchers._
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
      IdGen.generate[TestId, R](gen)
    }
  }

}
