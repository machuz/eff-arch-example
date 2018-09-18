package example.shared.adapter.config.di

import com.google.inject.AbstractModule

import akka.actor.ActorSystem

// Play以外のAdapterでActorSystemが必要になった時に使用する
class ActorSystemModule extends AbstractModule {

  def configure(): Unit = {
    // TODO: configからActorSystem名を取得する
    bind(classOf[ActorSystem]).toInstance(ActorSystem.apply("grpc"))
  }

}
