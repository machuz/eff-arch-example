package example.shared.lib.eff.util.idGen.interpreter

import com.google.inject.Inject

import org.atnos.eff._
import org.atnos.eff.interpret._
import org.atnos.eff.syntax.eff._

import jp.eigosapuri.es.shared.lib.dddSupport.domain.UUIDIdGenerator
import jp.eigosapuri.es.shared.lib.eff.util.idGen.IdGen
import jp.eigosapuri.es.shared.lib.eff.util.idGen.IdGen.Generate

abstract class IdGenInterpreter {
  def run[R, U, A](effects: Eff[R, A])(
    implicit m: Member.Aux[IdGen, R, U]
  ): Eff[U, A]
}

class IdGenInterpreterImpl @Inject()(
  uuidGen: UUIDIdGenerator
) extends IdGenInterpreter {

  def run[R, U, A](effects: Eff[R, A])(
    implicit m: Member.Aux[IdGen, R, U]
  ): Eff[U, A] = {
    translate(effects)(new Translate[IdGen, U] {
      def apply[X](i: IdGen[X]): Eff[U, X] = {
        i match {
          case Generate(idGenerator) =>
            for {
              id <- {
                idGenerator
                  .generate(uuidGen.generate.toString)
                  .pureEff[U]
              }
            } yield id
        }
      }
    })
  }

}
