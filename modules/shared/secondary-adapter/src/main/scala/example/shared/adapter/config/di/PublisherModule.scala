package example.shared.adapter.config.di

import com.google.inject.{ AbstractModule, TypeLiteral }

import slick.dbio.DBIO

import jp.eigosapuri.es.shared.adapter.secondary.publisher.{
  PushNotificationPublisherImpl => OldPushNotificationPublisherImpl
}
import jp.eigosapuri.es.shared.interface.publisher.notification.push.{
  PushNotificationPublisher => OldPushNotificationPublisher
}

class PublisherModule extends AbstractModule {

  def configure(): Unit = {
    bind(new TypeLiteral[OldPushNotificationPublisher[DBIO]]() {}).to(classOf[OldPushNotificationPublisherImpl])
//    bind(classOf[PushNotificationPublisher]).to(classOf[PushNotificationPublisherImpl])
  }
}
