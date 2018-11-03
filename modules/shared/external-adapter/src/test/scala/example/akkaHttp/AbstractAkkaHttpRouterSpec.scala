package example.akkaHttp

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{ Matchers, WordSpecLike }

class AbstractAkkaHttpRouterSpec extends WordSpecLike with Matchers with ScalatestRouteTest {

  val router = new AbstractAkkaHttpRouter

  "hello route" should {

    "hello!!" in {

      Get("/hello?name=ME") ~> router.helloRoute ~> check {
        responseAs[String] shouldBe "Hello ME!!"
      }
    }
  }
}
