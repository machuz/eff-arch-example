package example.shared.lib.test.eff.push.interpreter

import org.atnos.eff.{ <=, Eff, SideEffect }
import org.atnos.eff.interpret.interpretUnsafe

import jp.eigosapuri.es.shared.lib.eff.push.PushIO
import jp.eigosapuri.es.shared.lib.eff.push.PushIO.{ Publish, PublishSilent }

class PushIOTestInterpreter {
  def run[R, A](effects: Eff[R, A])(implicit m: PushIO <= R): Eff[m.Out, A] = {

    import cats.implicits._

    val sideEffect = new SideEffect[PushIO] {
      def apply[X](kv: PushIO[X]): X =
        kv match {
          case Publish(push) =>
            println(s"publish($push)")
            ().asInstanceOf[X]

          case PublishSilent(push) =>
            println(s"publish($push)")
            ().asInstanceOf[X]

        }
      def applicative[X, Tr[_]: cats.Traverse](ms: Tr[PushIO[X]]): Tr[X] =
        ms.map(apply)
    }
    interpretUnsafe(effects)(sideEffect)(m)
  }
}
