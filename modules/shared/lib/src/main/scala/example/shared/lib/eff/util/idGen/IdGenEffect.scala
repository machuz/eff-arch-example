package example.shared.lib.eff.util.idGen

import org.atnos.eff.{ Eff, Member }

import example.shared.lib.eff.util.idGen.interpreter.IdGenInterpreter

trait IdGenOps {

  implicit class IdGenOps[R, A](effects: Eff[R, A]) {
    def runIdGen[U](
      implicit interpreter: IdGenInterpreter,
      member1: Member.Aux[IdGen, R, U]
    ): Eff[U, A] = interpreter.run(effects)
  }
}

trait IdGenEffect extends IdGenOps with IdGenTypes
