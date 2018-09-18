package jp.eigosapuri.es.shared.adapter.secondary.slick.extension

import slick.dbio.DBIO

import scala.concurrent.ExecutionContext.Implicits.global

import scalaz.{ -\/, \/-, EitherT }

class SeqExtensionsSpec extends ExtensionSpecification {

  "SeqExtensions" should {

    "SeqEitherTExtension.sequence" must {

      import SeqExtensions._

      "left" in {

        val seq = Seq(
          EitherT[DBIO, String, String] {
            DBIO.successful {
              \/-("right")
            }
          },
          EitherT[DBIO, String, String] {
            DBIO.successful {
              -\/("left")
            }
          }
        )

        val ret = await(run(seq.sequence.run))

        ret must be(-\/("left"))
      }

      "right" in {

        val seq = Seq(
          EitherT[DBIO, String, String] {
            DBIO.successful {
              \/-("right1")
            }
          },
          EitherT[DBIO, String, String] {
            DBIO.successful {
              \/-("right2")
            }
          }
        )
        val ret = await(run(seq.sequence.run))

        ret must be(\/-(Seq("right1", "right2")))
      }
    }
  }
}
