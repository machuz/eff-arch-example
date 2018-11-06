package example.config.di
import com.google.inject.AbstractModule
import example.{ExampleRouter, UserController}

class ExampleApiExternalAdapterModule extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[ExampleRouter])
    bind(classOf[UserController])
  }

}
