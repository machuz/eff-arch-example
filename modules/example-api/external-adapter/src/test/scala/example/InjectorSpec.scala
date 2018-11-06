package example

import example.config.di.Injector
import example.shared.lib.test.AbstractSpecification

class InjectorSpec extends AbstractSpecification with Injector {

  "Application" should {

    "run" in {
      injector.getInstance(classOf[ExampleApiServer]) // InjectできればOK
      true must be(true)
    }
  }
}
