package jp.eigosapuri.es.shared.adapter.secondary.slick.extension

import slick.dbio.DBIO

import scala.concurrent.ExecutionContext.Implicits.global

import scalaz.{ -\/, \/- }

class DBIOExtensionsSpec extends ExtensionSpecification {

  "DBIOExtensions" should {

    import DBIOExtensions._

    "DBIOEitherExtension.toEitherT" must {

      "left" in {

        val entity = DBIO.successful(-\/("left"))

        val ret = await(run(entity.toEitherT.run))

        ret must be(-\/("left"))
      }

      "right" in {

        val entity = DBIO.successful(\/-("right"))

        val ret = await(run(entity.toEitherT.run))

        ret must be(\/-("right"))
      }
    }
  }
}
