package example.shared.lib.extension

import scala.concurrent.Future

import scalaz.Scalaz.{ none, vectorMonoid, ToOptionIdOps, ToWriterOps }
import scalaz.{ -\/, \/-, WriterT }

class WriterExtensionsSpec extends ExtensionSpecification {

  "WriterExtensions" should {

    "toEither" must {

      import WriterExtensions._

      "left" in {
        val seq = none[Int].set(Vector("str1"))

        val ret = seq.toEither
        ret must be(-\/(Vector("str1")))
      }

      "right" in {
        val seq = 1.some.set(Vector())

        val ret = seq.toEither
        ret must be(\/-(1))
      }
    }

    "toWriteT" must {

      import WriterExtensions._

      "None" in {
        val seq = none[Int].set(Vector("str1"))

        val ret = await(seq.toWriterT.run)
        ret must be((Vector("str1"), None))
      }

      "Some" in {
        val seq = 1.some.set(Vector())

        val ret = await(seq.toWriterT.run)
        ret must be((Vector(), Some(1)))
      }
    }
  }
}
