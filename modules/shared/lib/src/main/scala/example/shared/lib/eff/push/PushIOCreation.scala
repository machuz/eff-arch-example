package example.shared.lib.eff.push

import org.atnos.eff.Eff

import jp.eigosapuri.es.shared.domain.notification.push.Push
import jp.eigosapuri.es.shared.lib.eff.push.PushIO.{ Publish, PublishSilent }
import jp.eigosapuri.es.shared.lib.eff.push.PushIOTypes._pushio

trait PushIOCreation {

  def publish[R: _pushio](
    value: Push
  ): Eff[R, Unit] =
    Eff.send[PushIO, R, Unit](Publish(value))

  def publishSilent[R: _pushio](
    value: Push
  ): Eff[R, Unit] =
    Eff.send[PushIO, R, Unit](PublishSilent(value))

}
