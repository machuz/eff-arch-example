package example.shared.lib.extension

import scalaz.\/-

class EitherExtensionsSpec extends ExtensionSpecification {

  "EitherExtensions" should {

    "toEitherT" must {

      import EitherExtensions._

      "right" in {

        val either = \/-("right")

        val ret = await(either.toEitherT.run)

        ret must be(\/-("right"))
      }
    }
  }
}
