package example.config.di
import com.google.inject.AbstractModule
import example.ExampleController

class ExampleApiExternalAdapterModule extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[ExampleController])
  }

}
