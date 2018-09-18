package example.shared.lib.eff.util.idGen

import org.atnos.eff.{ Eff, Fx, Member }

import jp.eigosapuri.es.shared.lib.eff.util.idGen.interpreter.IdGenInterpreter

object IdGenInterpretationTypes {
  type IdGenStack = Fx.fx1[IdGen]
}

trait IdGenOps {

  implicit class IdGenOps[R, A](effects: Eff[R, A]) {
    def runIdGen[U](
      implicit interpreter: IdGenInterpreter,
      member1: Member.Aux[IdGen, R, U]
    ): Eff[U, A] = interpreter.run(effects)
  }
}

object IdGenEffect extends IdGenOps with IdGenCreation {}
