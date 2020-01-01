package example.config.di
import com.google.inject.AbstractModule

import example.ExampleRouter
import example.user.UserController

class ExampleApiExternalAdapterModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[ExampleRouter])
    bind(classOf[UserController])
  }

}
