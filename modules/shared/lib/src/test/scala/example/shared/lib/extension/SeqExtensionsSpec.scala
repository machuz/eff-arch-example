package example.shared.lib.extension

import scala.concurrent.Future

import scalaz.Scalaz._
import scalaz.{ -\/, \/-, EitherT, Monad, WriterT }

class SeqExtensionsSpec extends ExtensionSpecification {

  "SeqExtensions" should {

    "SeqOptionExtension.sequence" must {

      import SeqExtensions._

      "left" in {

        val seq = Seq(Some("some"), None)

        val ret = seq.sequence

        ret must be(None)
      }

      "right" in {

        val seq = Seq(Some("right1"), Some("right2"))

        val ret = seq.sequence

        ret must be(Some(Seq("right1", "right2")))
      }
    }

    "SeqEitherExtension.sequence" must {

      import SeqExtensions._

      "left" in {

        val seq = Seq(\/-("right"), -\/("left"))

        val ret = seq.sequence

        ret must be(-\/("left"))
      }

      "right" in {

        val seq = Seq(\/-("right1"), \/-("right2"))

        val ret = seq.sequence

        ret must be(\/-(Seq("right1", "right2")))
      }
    }

    "SeqEitherTExtension.sequence" must {

      import SeqExtensions._

      "left" in {

        val seq = Seq(
          EitherT[Future, String, String] {
            Future.successful {
              \/-("right")
            }
          },
          EitherT[Future, String, String] {
            Future.successful {
              -\/("left")
            }
          }
        )

        val ret = await(seq.sequence.run)

        ret must be(-\/("left"))
      }

      "right" in {

        val seq = Seq(
          EitherT[Future, String, String] {
            Future.successful {
              \/-("right1")
            }
          },
          EitherT[Future, String, String] {
            Future.successful {
              \/-("right2")
            }
          }
        )
        val ret = await(seq.sequence.run)

        ret must be(\/-(Seq("right1", "right2")))
      }
    }

    "SeqWriterExtension.sequence" must {

      import SeqExtensions._

      "None" in {
        val seq = Seq(none[Int].set(Vector("str1")), none[Int].set(Vector("str2")))

        val ret = seq.sequence
        ret.run must be(Vector("str1", "str2"), None)
      }

      "Some" in {

        val seq = Seq(1.some.set(Vector()), 2.some.set(Vector()))

        val ret = seq.sequence
        ret.run must be(Vector(), Some(Seq(1, 2)))
      }
    }

    "SeqWriterTExtension.sequence" must {

      import SeqExtensions._

      "None" in {
        val seq = Seq(
          WriterT[Future, Vector[String], Option[Int]] {
            Future.successful {
              (Vector("str1"), none)
            }
          },
          WriterT[Future, Vector[String], Option[Int]] {
            Future.successful {
              (Vector("str2"), none)
            }
          }
        )

        val ret = await(seq.sequence.run)
        ret must be(Vector("str1", "str2"), None)
      }

      "Some" in {

        val seq = Seq(
          WriterT[Future, Vector[String], Option[Int]] {
            Future.successful {
              (Vector(), 1.some)
            }
          },
          WriterT[Future, Vector[String], Option[Int]] {
            Future.successful {
              (Vector(), 2.some)
            }
          }
        )
        val ret = await(seq.sequence.run)
        ret must be(Vector(), Some(Seq(1, 2)))
      }
    }

    "SeqMonadExtension.sequence" must {

      import SeqExtensions._

      "right[Option]" in {
        implicit val m = Monad[Option]

        val seq: Seq[Option[String]] = Seq("a".some, "b".some)

        val ret = seq.sequence

        ret must be(Seq("a", "b").some)
      }

      "left[Option]" in {
        implicit val m = Monad[Option]

        val seq: Seq[Option[String]] = Seq("a".some, None)

        val ret = seq.sequence

        ret must be(None)
      }

    }
  }
}
