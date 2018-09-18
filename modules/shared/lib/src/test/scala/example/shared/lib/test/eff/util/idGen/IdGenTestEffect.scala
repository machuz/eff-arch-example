package example.shared.lib.test.eff.util.idGen

import com.eaio.uuid.UUID

import org.atnos.eff.{ Eff, Member }

import jp.eigosapuri.es.shared.lib.dddSupport.domain.UUIDIdGenerator
import jp.eigosapuri.es.shared.lib.eff.util.idGen.{ IdGen, IdGenCreation }
import jp.eigosapuri.es.shared.lib.eff.util.idGen.interpreter.{ IdGenInterpreter, IdGenInterpreterImpl }

trait IdGenTestOps {

  val testUUID = "123e4567-e89b-12d3-a456-556642440000"

  implicit class IdGenTestOps[R, A](effects: Eff[R, A]) {
    def testRunIdGen[U](
      implicit m1: Member.Aux[IdGen, R, U]
    ): Eff[U, A] = {
      val g: UUIDIdGenerator = new UUIDIdGenerator {
        def generate: UUID = new UUID(testUUID)
      }
      val interpreter = new IdGenInterpreterImpl(g)
      interpreter.run(effects)
    }
  }

}

object IdGenTestEffect extends IdGenTestOps with IdGenCreation {}
