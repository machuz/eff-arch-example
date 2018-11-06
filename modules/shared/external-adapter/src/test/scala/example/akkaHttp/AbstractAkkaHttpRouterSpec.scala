package example.akkaHttp

import akka.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.{ Matchers, WordSpecLike }

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{ complete, parameter, path }
import akka.http.scaladsl.server.Route

class AbstractAkkaHttpRouterSpec extends WordSpecLike with Matchers with ScalatestRouteTest {

  val router = new TestRouter

  "hello route" should {

    "hello!!" in {

      Get("/hello?name=ME") ~> router.routes ~> check {
        responseAs[String] shouldBe "Hello ME!!"
      }
    }
  }
}

class TestRouter extends AbstractAkkaHttpRouter {
  // nameパラメータを受け付けテキストを返信する
  override def routes: Route =  path("hello") {
    parameter('name) { name =>
      complete(StatusCodes.OK -> s"Hello $name!!")
    }
  }
}
