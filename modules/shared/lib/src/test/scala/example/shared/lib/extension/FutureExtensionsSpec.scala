package example.shared.lib.extension

import scala.concurrent.Future

import scalaz.Scalaz.vectorMonoid

class FutureExtensionsSpec extends ExtensionSpecification {

  "FutureExtensions" should {

    "writeT" must {

      import FutureExtensions._

      "None" in {

        val f = Future.successful {
          None
        }

        val ret = await(f.writeT(Vector("str")).run)

        ret must be((Vector("str"), None))
      }

      "Some" in {

        val f = Future.successful {
          Some("some")
        }

        val ret = await(f.writeT(Vector("str")).run)

        ret must be((Vector(), Some("some")))
      }
    }
  }
}
