package example.shared.lib.extension

import example.shared.lib.adapter.secondary.repository.TestTaskRunner

import scala.concurrent.duration.Duration
import scala.concurrent.Await

import scalaz.{ Monad, \/- }
import scalaz.Scalaz._

class MonadExtensionsSpec extends ExtensionSpecification {

  "MonadExtensions" should {

    "run" must {
      import MonadExtensions._

      implicit val taskRunner     = new TestTaskRunner
      val value: Identity[String] = "io".point[Identity]
      val ret                     = Await.result(value.run, Duration.Inf)

      ret must be("io")
    }

    "runTransaction" must {
      import MonadExtensions._

      implicit val taskRunner     = new TestTaskRunner
      val value: Identity[String] = "io".point[Identity]

      val ret = Await.result(value.runTransaction, Duration.Inf)

      ret must be("io")
    }

    "toEitherT" must {

      import MonadExtensions._

      "right" in {

        implicit val M = Monad[Option]
        val either     = \/-("right").point[Option]

        val ret = either.toEitherT.run

        ret must be(Some(\/-("right")))
      }
    }
  }
}
