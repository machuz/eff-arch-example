package example.shared.lib.test.eff.push

import org.atnos.eff._

import example.shared.lib.test.eff.push.interpreter.PushIOTestInterpreter

import jp.eigosapuri.es.shared.lib.eff.push.{ PushIO, PushIOCreation }
import jp.eigosapuri.es.shared.lib.test.eff.push.interpreter.PushIOTestInterpreter

class PushIOTestEffect {}

trait PushTestIOOps extends PushIOTestInterpreter {
  implicit class PushOps[R, A](effects: Eff[R, A]) {
    def testRunPushIO[U](
      implicit m: PushIO <= R
    ) = run(effects)
  }
}

object PushIOTestEffect extends PushTestIOOps with PushIOCreation {
  type TestPushIOStack = Fx.fx1[PushIO]
}
