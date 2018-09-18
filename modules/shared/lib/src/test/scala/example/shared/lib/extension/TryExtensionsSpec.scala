package example.shared.lib.extension

import scala.util.{ Failure, Success }

class TryExtensionsSpec extends ExtensionSpecification {

  "TryExtensions" should {

    "writeT" must {

      import TryExtensions._

      "Failure" in {

        var retVar = 1
        Failure {
          retVar = 3
          new Exception()
        }.Finally {
          retVar = 2
        }

        retVar must be(2)
      }

      "Success" in {

        var retVar = 1
        Success {
          retVar = 3
          1
        }.Finally {
          retVar = 2
        }

        retVar must be(2)
      }
    }
  }
}
