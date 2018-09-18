package example.shared.lib.util

import monix.eval.Task

import scala.concurrent.duration.FiniteDuration

import scalaz.Scalaz.ToEitherOps
import scalaz.{ -\/, \/ }

case class UnExpectedException(message: String = "") extends Exception(message)

case class RetryException(throwables: List[Throwable]) extends Exception(throwables.map(_.getMessage).mkString(","))

object RetryUtils {

  import scala.util.control.Exception.allCatch

  /**
    * 渡した関数を指定回数、指定間隔でretryする
    * @param retryLimit retry回数
    * @param retryInterval retry間隔(ms)
    * @param expectedValueOpt 期待する値 この値が返るまでは成功とみなさない
    * @param f 渡す関数
    * @tparam T 返り値
    * @return
    */
  def retry[T](retryLimit: Int, retryInterval: Int, expectedValueOpt: Option[T])(f: => T): \/[Throwable, T] =
    retry(retryLimit, retryInterval, expectedValueOpt, classOf[Throwable])(f)

  /**
    * 渡した関数を指定回数、指定間隔でretryする(例外型指定)
    * @param retryLimit retry回数
    * @param retryInterval retry間隔(ms)
    * @param expectedValueOpt 期待する値 この値が返るまでは成功とみなさない
    * @param catchExceptionClasses catchする例外の型。指定した型以外の例外が返った時点でretry中止
    * @param f 渡す関数
    * @tparam T 返り値
    * @return
    */
  def retry[T](retryLimit: Int, retryInterval: Int, expectedValueOpt: Option[T], catchExceptionClasses: Class[_]*)(
    f: => T
  ): \/[Throwable, T] =
    retry(
      retryLimit,
      retryInterval,
      expectedValueOpt,
      e => catchExceptionClasses.exists(_.isAssignableFrom(e.getClass))
    )(f)

  private def retry[T](
    retryLimit: Int,
    retryInterval: Int,
    expectedValueOpt: Option[T],
    shouldCatch: Throwable => Boolean
  )(f: => T): \/[Throwable, T] = {
    @annotation.tailrec
    def retry0(errors: List[Throwable], f: => T): \/[Throwable, T] = {
      allCatch.either(f) match {
        case Right(r) if expectedValueOpt.isEmpty => r.right
        case Right(r) =>
          val expectedValue = expectedValueOpt.get
          r match {
            case _ if expectedValue == r => r.right
            case _ if errors.size < retryLimit - 1 =>
              Thread.sleep(retryInterval)
              val e = UnExpectedException(s"""require:[${expectedValue.toString}] found:[${r.toString}]""")
              retry0(e :: errors, f)
            case _ =>
              val e = UnExpectedException(s"""require:[${expectedValue.toString}] found:[${r.toString}]""")
              RetryException(e :: errors).left
          }
        case Left(e) =>
          shouldCatch(e) match {
            case true if errors.size < retryLimit - 1 =>
              Thread.sleep(retryInterval)
              retry0(e :: errors, f)
            case true =>
              RetryException(e :: errors).left
            case _ =>
              e.left
          }
      }
    }
    retry0(Nil, f)
  }

  def retryBackoff[A](source: Task[A], maxRetries: Int, firstDelay: FiniteDuration): Task[A] = {

    source.onErrorHandleWith {
      case ex: Exception =>
        if (maxRetries > 0)
          retryBackoff(source, maxRetries - 1, firstDelay * 2)
            .delayExecution(firstDelay)
        else
          Task.raiseError(ex)
    }
  }

}
