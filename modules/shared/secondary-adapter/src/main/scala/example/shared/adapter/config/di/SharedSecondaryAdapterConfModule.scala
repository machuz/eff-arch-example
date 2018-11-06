package example.shared.adapter.config.di

import com.google.inject.AbstractModule

import example.shared.adapter.config.AkkaConf

class SharedSecondaryAdapterConfModule extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[AkkaConf])
  }

}
