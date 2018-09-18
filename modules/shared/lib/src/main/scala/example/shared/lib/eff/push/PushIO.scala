package example.shared.lib.eff.push

import jp.eigosapuri.es.shared.domain.notification.push.Push

sealed abstract class PushIO[+A]

object PushIO extends PushIOCreation {

  case class Publish(value: Push) extends PushIO[Unit]

  case class PublishSilent(value: Push) extends PushIO[Unit]

}
