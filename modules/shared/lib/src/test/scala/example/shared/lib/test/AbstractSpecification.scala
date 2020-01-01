package example.shared.lib.test

import com.eaio.uuid.UUID

import org.joda.time.{ DateTime, DateTimeZone, LocalDate }
import org.joda.time.{ DateTime, LocalDate }
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.words.MatcherWords
import org.scalatest.{ EitherValues, MustMatchers, OptionValues, WordSpec }

import monix.eval.Task
import monix.execution.Scheduler

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Awaitable, Future }
import scala.util.Random
import example.shared.lib.dddSupport.domain.{ EsRandom, Identifier, UUIDIdGenerator }

/**
  * test用の抽象クラス
  */
abstract class AbstractSpecification
  extends WordSpec
  with ScalaFutures
  with MatcherWords
  with MustMatchers
  with EitherValues
  with OptionValues {

  import scala.concurrent.ExecutionContext.Implicits.global

  val uuid = "123e4567-e89b-12d3-a456-556642440000"

  // 常に同じUUIDしか出ないようにしておく
  implicit val idGenerator: UUIDIdGenerator = new UUIDIdGenerator {
    def generate: UUID = new UUID(uuid)
    def generateA[A]: A =
      new Identifier[String] {
        override val value: String = uuid
      }.asInstanceOf[A]
  }

  // seed が同じ場合は同じ値になる
  implicit val esRandom: EsRandom = new EsRandom {
    override val random: Random = new Random(1)

    // 256 characters (現時点で127文字より大きいランダム文字列を作成する箇所がないため)
    val randomString =
      "NAvZuGESoIJ7hbqOIsAV4iWta9qh1yp4iuhRxkraBq7ZFYeOIN8pKbyLI3gOYbIvKewK3Q3sDEuVsXZDyiFctJPfRA9OuKpfNN0gKQQJgU6vtvtMPRlk3xsz6PuqE63HExgxmnDEctw6mulO1MVzwfimlHnhngDXDcpxHwjGYL1Aof2KCdlEu2lq4L6r4n3Cm7f4BXngmbFmSBoDCFwd4L2mtVgUsRbqWMisFA6XOycHPo86djozNkogG3315waO"
    override def generateRandomString(length: Int): String = randomString.take(length)

  }

  protected[this] def await[T](a: Awaitable[T]): T = Await.result(a, Duration.Inf)

  protected[this] def await[T](a: Task[T])(implicit s: Scheduler): T = await(a.runToFuture)

  protected[this] def awaitOrRecover[T](future: Future[T]): Option[T] =
    await(future.map(Some(_)).recover { case _ => None })

  protected[this] def awaitOrRecover[T](future: Future[T], t: T): T =
    awaitOrRecover(future).getOrElse(t)

  protected[this] def currentDate: LocalDate = new LocalDate(2018, 7, 23)

  protected[this] def currentDateTime: DateTime = new DateTime(2018, 7, 23, 9, 0, DateTimeZone.UTC)

  protected[this] lazy val random = new Random()

  protected[this] def randomString(n: Int): String = random.alphanumeric.take(n).mkString

  protected[this] def randomInt: Int = random.nextInt()

  protected[this] def randomLong: Long = random.nextLong()

  protected[this] def randomDouble: Double = random.nextDouble()

  protected[this] def randomBoolean: Boolean = random.nextBoolean()

  protected[this] def randomDate: LocalDate = randomDateTime.toLocalDate

  protected[this] def randomDateTime: DateTime = currentDateTime.plus(randomInt)

  protected[this] def values[A](fa: Future[A], duration: Duration = Duration.Inf): A =
    Await.result(fa, duration)

}
