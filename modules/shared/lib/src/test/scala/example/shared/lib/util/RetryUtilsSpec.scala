package example.shared.lib.util

import example.shared.lib.test.AbstractSpecification

import scalaz.{ -\/, \/, \/- }

class RetryUtilsSpec extends AbstractSpecification {

  "RetryUtils" should {

    "retry [catch all exception]" must {
      "success" in {
        var i = 0
        val ret: \/[Throwable, Boolean] = RetryUtils.retry[Boolean](3, 500, None) {
          i = i + 1
          i match {
            case 3 => true
            case _ => throw new RuntimeException("error_" + i)
          }
        }
        ret must be(\/-(true))
      }

      "failed" in {
        val ret: \/[Throwable, Unit] = RetryUtils.retry[Unit](3, 500, None) {
          throw new RuntimeException("error")
        }
        ret.leftMap(_.getMessage) must be(-\/("error,error,error"))
      }

      "success - some(expectedValue)" in {
        val expectedValueOpt: Some[String] = Some("expected")
        var i                              = 0
        val ret: \/[Throwable, String] = RetryUtils.retry[String](3, 0, expectedValueOpt) {
          i = i + 1
          i match {
            case 1 => "unexpected" // unexpectedが返ってもRetry続行
            case 2 => "expected"
            case _ => throw new RuntimeException("error")
          }
        }
        ret must be(\/-("expected"))
      }
    }

    "retry [catch defined exception]" must {
      "catch NullPointerException,UnsupportedOperationException" in {
        var i = 0
        val ret: \/[Throwable, Boolean] = RetryUtils
          .retry[Boolean](6, 0, None, classOf[NullPointerException], classOf[UnsupportedOperationException]) {
            i = i + 1
            i match {
              case 1 => throw new NullPointerException("error_" + i)
              case 2 => throw new UnsupportedOperationException("error_" + i)
              case 3 => throw new IllegalArgumentException("error_" + i)
              case 4 => throw new RuntimeException("error_" + i) // 指定していないIllegalArgumentが返った時点でRetry停止
              case _ => throw new RuntimeException("error_" + i)
            }
          }
        ret.leftMap(_.getMessage) must be(-\/("error_3"))
      }
    }

  }

}
