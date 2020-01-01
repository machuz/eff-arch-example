package example.config.di

import com.google.inject.AbstractModule

import example.config.AkkaHttpServerConf

class SharedExternalAdapterConfModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[AkkaHttpServerConf])
  }

}
