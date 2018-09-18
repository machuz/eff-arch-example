package example.shared.lib.extension

import scalaz.Scalaz.vectorMonoid

class OptionExtensionsSpec extends ExtensionSpecification {

  "OptionExtensions" should {

    "write" must {

      import OptionExtensions._

      "None" in {

        val o = None

        val ret = o.write(Vector("str"))

        ret.run must be((Vector("str"), None))
      }

      "Some" in {

        val o = Some("some")

        val ret = o.write(Vector("str"))

        ret.run must be((Vector(), Some("some")))
      }
    }
  }
}
