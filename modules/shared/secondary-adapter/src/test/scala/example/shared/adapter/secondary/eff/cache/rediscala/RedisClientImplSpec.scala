package jp.eigosapuri.es.shared.adapter.secondary.eff.cache.rediscala

import org.mockito.{ Matchers, Mockito }

import akka.actor.ActorSystem
import akka.util.ByteString
import io.circe.{ Decoder, Encoder }
import monix.eval.Task
import redis.protocol.Bulk
import redis.{ ByteStringFormatter, RedisClient => RediscalaClient, Cursor }

import scala.concurrent.Future

import scalaz.{ -\/, \/, \/-, Tag }
import scalaz.Scalaz.{ ToEitherOps, ToOptionIdOps }

import jp.eigosapuri.es.shared.adapter.config.SharedAdapterConf.{ redis, _ }
import jp.eigosapuri.es.shared.lib.dddSupport.ErrorCode
import jp.eigosapuri.es.shared.lib.dddSupport.EsError.NonFatalError
import jp.eigosapuri.es.shared.lib.eff.cache.CacheIOTypes._
import jp.eigosapuri.es.shared.lib.dddSupport.EsError
import jp.eigosapuri.es.shared.lib.dddSupport.domain.cache.{ CacheableId, CacheableModel, CacheableValueObject }
import jp.eigosapuri.es.shared.lib.dddSupport.domain.{ Identifier, ValueObject }
import jp.eigosapuri.es.shared.lib.eff.cache.RedisClient
import jp.eigosapuri.es.shared.lib.test.{ AbstractSpecification, DeterministicTestObject }
import jp.eigosapuri.es.shared.lib.eff.cache.RedisClient._

class RedisClientImplSpec extends AbstractSpecification {
  import Matchers._
  import Mockito._

  import monix.execution.Scheduler.Implicits.global

  import RedisClientImplSpec._

  println("---------------------------------------------")
  println(redis.use)
  println(redis.master.host)
  println("---------------------------------------------")

  if (redis.use && redis.master.host == "127.0.0.1") {

    trait LocalSetUp {
      val (_, (testModel1, testModel2)) = (for {
        testModel1 <- DeterministicTestObject[TestModel1]
        testModel2 <- DeterministicTestObject[TestModel2]
      } yield {
        (
          testModel1,
          testModel2
        )
      }).apply(0)

      val c = new RedisClientImpl(akka.actor.ActorSystem())(
        redis.master.host,
        redis.master.port,
        redis.master.password,
        redis.master.dbNum
      )

      implicit val fmt1: ByteStringFormatter[TestModel1] = byteStringFormatter[TestModel1]
      implicit val fmt2: ByteStringFormatter[TestModel2] = byteStringFormatter[TestModel2]
      val expireSeconds: Option[Long]                    = 10L.some
      val cacheKeyGlob: CacheKeyGlob                     = CacheKeyGlob("*")
    }

    "RedisClientImpl(local)" should {
      "all" must {
        "successful" in new LocalSetUp {
          val actual =
            for {
              put1 <- c.put(
                CacheKey("t1_test_1"),
                testModel1,
                expireSeconds
              )
              put2 <- c.put(
                CacheKey("t1_test_2"),
                testModel1,
                expireSeconds
              )
              scanAll <- c.scan(CacheKeyGlob("*"))(fmt1)
              put3 <- c.put(
                CacheKey("t2_test_3"),
                testModel2,
                expireSeconds
              )
              put4 <- c.put(
                CacheKey("t2_test_4"),
                testModel2,
                expireSeconds
              )
              existsDelBefore    <- c.exists(CacheKey("t2_test_3"))
              get                <- c.get(CacheKey("t2_test_3"))(fmt2)
              delete             <- c.delete(CacheKey("t2_test_3"))
              existsDelAfter     <- c.exists(CacheKey("t2_test_3"))
              clear1             <- c.clear
              clear2             <- c.clear
              emptyClear         <- c.clear
              scanAllClearAfter1 <- c.scan(CacheKeyGlob("*"))(fmt1)
              scanAllClearAfter2 <- c.scan(CacheKeyGlob("*"))(fmt1)
              putList <- c.putList[TestModel1](
                CacheKey("list_1"),
                Seq(testModel1, testModel1, testModel1),
                expireSeconds
              )
              getList           <- c.getList[TestModel1](CacheKey("list_1"))
              clear3            <- c.clear
              getListClearAfter <- c.getList[TestModel1](CacheKey("list_1"))
              putHash <- c.putHash[TestModel2](
                CacheKey("hash_1"),
                CacheHashKey("hashKey_1"),
                testModel2
              )
              putHashBulk1 <- c.putBulkHash[TestModel2](
                CacheKey("hash_1"),
                Map(
                  CacheHashKey("1_hashKey_1") -> testModel2,
                  CacheHashKey("1_hashKey_2") -> testModel2,
                  CacheHashKey("1_hashKey_3") -> testModel2
                )
              )
              putHashBulk2 <- c.putBulkHash[TestModel2](
                CacheKey("hash_1"),
                Map(
                  CacheHashKey("2_hashKey_1") -> testModel2,
                  CacheHashKey("2_hashKey_2") -> testModel2,
                  CacheHashKey("2_hashKey_3") -> testModel2
                )
              )
              scanHash1 <- c.scanHash[TestModel2](
                CacheKey("hash_1"),
                CacheHashKeyGlob("1_*")
              )
              deleteHash <- c.deleteHash(
                CacheKey("hash_1"),
                Seq(CacheHashKey("2_hashKey_1"), CacheHashKey("2_hashKey_2"))
              )
              scanHash2 <- c.scanHash[TestModel2](
                CacheKey("hash_1"),
                CacheHashKeyGlob("2_*")
              )
              scanHashAll <- c.scanHash[TestModel2](
                CacheKey("hash_1"),
                CacheHashKeyGlob("*")
              )
              getHashAll <- c.getAllHash[TestModel2](
                CacheKey("hash_1")
              )
              clear4 <- c.clear

            } yield {
              put1 must be(\/-(testModel1))
              put2 must be(\/-(testModel1))
              scanAll must be(\/-(List(testModel1, testModel1)))
              put3 must be(\/-(testModel2))
              put4 must be(\/-(testModel2))
              existsDelBefore must be(\/-(true))
              get must be(\/-(Option(testModel2)))
              delete must be(\/-(()))
              existsDelAfter must be(\/-(false))
              clear1 must be(\/-(()))
              clear2 must be(\/-(()))
              emptyClear must be(\/-(()))
              scanAllClearAfter1 must be(\/-(Vector()))
              scanAllClearAfter2 must be(\/-(Vector()))
              putList must be(\/-(List(testModel1, testModel1, testModel1)))
              getList.map(_.sortBy(_.id.value)) must be(\/-(Vector(testModel1, testModel1, testModel1)))
              clear3 must be(\/-(()))
              getListClearAfter must be(\/-(Vector()))
              putHash must be(\/-(testModel2))
              putHashBulk1 must be(\/-(Seq(testModel2, testModel2, testModel2)))
              putHashBulk2 must be(\/-(Seq(testModel2, testModel2, testModel2)))
              scanHash1 must be(\/-(List(testModel2, testModel2, testModel2)))
              deleteHash must be(\/-(()))
              scanHash2 must be(\/-(List(testModel2)))
              scanHashAll.map(_.size) must be(\/-(5))
              clear4 must be(\/-(()))
            }
          await(actual)
        }
      }
    }
  } else ()

  "RedisClientImpl(mock)" should {

    trait SetUp {
      val (_, (testModel1, testModel2)) = (for {
        testModel1 <- DeterministicTestObject[TestModel1]
        testModel2 <- DeterministicTestObject[TestModel2]
      } yield {
        (
          testModel1,
          testModel2
        )
      }).apply(0)

      val rediscalaClient: RediscalaClient = Mockito.mock(classOf[RediscalaClient])
      val system: ActorSystem              = Mockito.mock(classOf[ActorSystem])
      val c: RedisClient = new RedisClientImpl(system)(
        redis.master.host,
        redis.master.port,
        redis.master.password,
        redis.master.dbNum
      ) {
        override lazy val c: RediscalaClient = rediscalaClient
      }

      val successRes = Bulk(Some(ByteString("OK")))

      implicit val fmt1: ByteStringFormatter[TestModel1] = byteStringFormatter[TestModel1]
      implicit val fmt2: ByteStringFormatter[TestModel2] = byteStringFormatter[TestModel2]
      val expireSeconds: Option[Long]                    = 10L.some
      val fatalException                                 = new OutOfMemoryError("fatal error")
      val nonFatalException                              = new RuntimeException("non fatal error")
      val cacheKeyGlob: CacheKeyGlob                     = CacheKeyGlob("*")

      val cacheKey1         = CacheKey("testKey_1")
      val cacheKeyGlob1     = CacheKeyGlob("*Key_1")
      val cacheKey2         = CacheKey("testKey_2")
      val cacheKeyGlob2     = CacheKey("*Key_2")
      val cacheHashKey1     = CacheHashKey("testHashKey_1")
      val cacheHashKeyGlob1 = CacheHashKeyGlob("*HashKey_1")
      val cacheHashKey2     = CacheHashKey("testHashKey_2")
      val cacheHashKeyGlob2 = CacheHashKeyGlob("*HashKey_2")

      val vMap = Map(cacheHashKey1 -> testModel1, cacheHashKey2 -> testModel1)

    }

    "put" must {

      "be successful" in new SetUp {

        when(rediscalaClient.set(Tag.unwrap(cacheKey1), testModel1, expireSeconds)(fmt1)).thenReturn(
          Future.successful(true)
        )

        val actual: Task[\/[EsError, TestModel1]] =
          c.put(
            cacheKey1,
            testModel1,
            expireSeconds
          )

        val expected: \/[EsError, TestModel1] = \/-(testModel1)
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).set(any(), any(), any(), any(), any(), any())(any())
      }

      "be return `REDIS_COMMAND_ERROR` when `set` returned false" in new SetUp {

        when(rediscalaClient.set(Tag.unwrap(cacheKey1), testModel1, expireSeconds)(fmt1)).thenReturn(
          Future.successful(false)
        )

        val actual: Task[\/[EsError, TestModel1]] =
          c.put(
            cacheKey1,
            testModel1,
            expireSeconds
          )

        val expectedErrorCode: ErrorCode = ErrorCode.REDIS_COMMAND_ERROR

        await(actual) match {
          case -\/(e: NonFatalError) =>
            e.code must be(expectedErrorCode)
            e.underlying.isInstanceOf[RuntimeException] must be(true)
          case _ =>
            true must be(false) // テストを失敗させる
        }
        verify(rediscalaClient, times(1)).set(any(), any(), any(), any(), any(), any())(any())
      }

      "be return `REDIS_REQUEST_ERROR` when `set` non-fatal error" in new SetUp {
        when(rediscalaClient.set(Tag.unwrap(cacheKey1), testModel1, expireSeconds)(fmt1)).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, TestModel1]] =
          c.put(
            cacheKey1,
            testModel1,
            expireSeconds
          )

        val expected = -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).set(any(), any(), any(), any(), any(), any())(any())
      }

      "be throw `exception` when `set` fatal error" in new SetUp {
        when(rediscalaClient.set(Tag.unwrap(cacheKey1), testModel1, expireSeconds)(fmt1)).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, TestModel1]] =
          c.put(
            cacheKey1,
            testModel1,
            expireSeconds
          )

        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).set(any(), any(), any(), any(), any(), any())(any())
      }
    }

    "putList" must {

      "be successful" in new SetUp {

        val v = Seq(testModel1, testModel1)

        when(rediscalaClient.lpush(Tag.unwrap(cacheKey1), v: _*)).thenReturn(
          Future.successful(2L)
        )

        when(rediscalaClient.expire(Tag.unwrap(cacheKey1), expireSeconds.get)).thenReturn(
          Future.successful(true)
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.putList(
            cacheKey1,
            v,
            expireSeconds
          )

        val expected: \/[EsError, Seq[TestModel1]] = \/-(v)
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).lpush(any(), any())(any())
        verify(rediscalaClient, times(1)).expire(any(), any())
      }

      "be return `REDIS_REQUEST_ERROR` when `lpush` non-fatal error" in new SetUp {

        val v = Seq(testModel1, testModel1)

        when(rediscalaClient.lpush(Tag.unwrap(cacheKey1), v: _*)).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.putList(
            cacheKey1,
            v,
            expireSeconds
          )

        val expected = -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).lpush(any(), any())(any())
        verify(rediscalaClient, times(0)).expire(any(), any())
      }

      "be throw `exception` when `lpush` fatal error" in new SetUp {
        val v = Seq(testModel1, testModel1)

        when(rediscalaClient.lpush(Tag.unwrap(cacheKey1), v: _*)).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.putList(
            cacheKey1,
            v,
            expireSeconds
          )

        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).lpush(any(), any())(any())
        verify(rediscalaClient, times(0)).expire(any(), any())
      }

      "be return `REDIS_COMMAND_ERROR` when `expire` returned false" in new SetUp {

        val v = Seq(testModel1, testModel1)

        when(rediscalaClient.lpush(Tag.unwrap(cacheKey1), v: _*)).thenReturn(
          Future.successful(2L)
        )

        when(rediscalaClient.expire(Tag.unwrap(cacheKey1), expireSeconds.get)).thenReturn(
          Future.successful(false)
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.putList(
            cacheKey1,
            v,
            expireSeconds
          )

        val expectedErrorCode = ErrorCode.REDIS_COMMAND_ERROR

        await(actual) match {
          case -\/(e: NonFatalError) =>
            e.code must be(expectedErrorCode)
            e.underlying.isInstanceOf[RuntimeException] must be(true)
          case _ =>
            true must be(false) // テストを失敗させる
        }

        verify(rediscalaClient, times(1)).lpush(any(), any())(any())
        verify(rediscalaClient, times(1)).expire(any(), any())
      }

      "be return `REDIS_REQUEST_ERROR` when `expire` non-fatal error" in new SetUp {

        val v = Seq(testModel1, testModel1)

        when(rediscalaClient.lpush(Tag.unwrap(cacheKey1), v: _*)).thenReturn(
          Future.successful(2L)
        )

        when(rediscalaClient.expire(Tag.unwrap(cacheKey1), expireSeconds.get)).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.putList(
            cacheKey1,
            v,
            expireSeconds
          )

        val expected: \/[EsError, Seq[TestModel1]] =
          -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).lpush(any(), any())(any())
        verify(rediscalaClient, times(1)).expire(any(), any())
      }

      "be throw `exception` when `expire` fatal error" in new SetUp {

        val v = Seq(testModel1, testModel1)

        when(rediscalaClient.lpush(Tag.unwrap(cacheKey1), v: _*)).thenReturn(
          Future.successful(2L)
        )

        when(rediscalaClient.expire(Tag.unwrap(cacheKey1), expireSeconds.get)).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.putList(
            cacheKey1,
            v,
            expireSeconds
          )

        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).lpush(any(), any())(any())
        verify(rediscalaClient, times(1)).expire(any(), any())
      }
    }

    "putHash" must {

      "be successful" in new SetUp {

        when(rediscalaClient.hset(Tag.unwrap(cacheKey1), Tag.unwrap(cacheHashKey1), testModel1)(fmt1)).thenReturn(
          Future.successful(true)
        )

        when(rediscalaClient.expire(Tag.unwrap(cacheKey1), expireSeconds.get)).thenReturn(
          Future.successful(true)
        )

        val actual: Task[\/[EsError, TestModel1]] =
          c.putHash(
            cacheKey1,
            cacheHashKey1,
            testModel1,
            expireSeconds
          )

        val expected: \/[EsError, TestModel1] = \/-(testModel1)
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).hset(any(), any(), any())(any())
        verify(rediscalaClient, times(1)).expire(any(), any())
      }

      "be return `REDIS_COMMAND_ERROR` when `hset` returned false" in new SetUp {

        when(rediscalaClient.hset(Tag.unwrap(cacheKey1), Tag.unwrap(cacheHashKey1), testModel1)(fmt1)).thenReturn(
          Future.successful(false)
        )

        val actual: Task[\/[EsError, TestModel1]] =
          c.putHash(
            cacheKey1,
            cacheHashKey1,
            testModel1,
            expireSeconds
          )

        val expectedErrorCode: ErrorCode = ErrorCode.REDIS_COMMAND_ERROR

        await(actual) match {
          case -\/(e: NonFatalError) =>
            e.code must be(expectedErrorCode)
            e.underlying.isInstanceOf[RuntimeException] must be(true)
          case _ =>
            true must be(false) // テストを失敗させる
        }
        verify(rediscalaClient, times(1)).hset(any(), any(), any())(any())
        verify(rediscalaClient, times(0)).expire(any(), any())
      }

      "be return `REDIS_REQUEST_ERROR` when `hset` non-fatal error" in new SetUp {
        when(rediscalaClient.hset(Tag.unwrap(cacheKey1), Tag.unwrap(cacheHashKey1), testModel1)(fmt1)).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, TestModel1]] =
          c.putHash(
            cacheKey1,
            cacheHashKey1,
            testModel1,
            expireSeconds
          )

        val expected = -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).hset(any(), any(), any())(any())
        verify(rediscalaClient, times(0)).expire(any(), any())
      }

      "be throw `exception` when `hset` fatal error" in new SetUp {
        when(rediscalaClient.hset(Tag.unwrap(cacheKey1), Tag.unwrap(cacheHashKey1), testModel1)(fmt1)).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, TestModel1]] =
          c.putHash(
            cacheKey1,
            cacheHashKey1,
            testModel1,
            expireSeconds
          )

        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).hset(any(), any(), any())(any())
        verify(rediscalaClient, times(0)).expire(any(), any())
      }

      "be return `REDIS_REQUEST_ERROR` when `expire` non-fatal error" in new SetUp {

        when(rediscalaClient.hset(Tag.unwrap(cacheKey1), Tag.unwrap(cacheHashKey1), testModel1)(fmt1)).thenReturn(
          Future.successful(true)
        )

        when(rediscalaClient.expire(Tag.unwrap(cacheKey1), expireSeconds.get)).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, TestModel1]] =
          c.putHash(
            cacheKey1,
            cacheHashKey1,
            testModel1,
            expireSeconds
          )

        val expected: \/[EsError, TestModel1] = -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).hset(any(), any(), any())(any())
        verify(rediscalaClient, times(1)).expire(any(), any())
      }

      "be return `REDIS_REQUEST_ERROR` when `expire` returned false" in new SetUp {

        when(rediscalaClient.hset(Tag.unwrap(cacheKey1), Tag.unwrap(cacheHashKey1), testModel1)(fmt1)).thenReturn(
          Future.successful(true)
        )

        when(rediscalaClient.expire(Tag.unwrap(cacheKey1), expireSeconds.get)).thenReturn(
          Future.successful(false)
        )

        val actual: Task[\/[EsError, TestModel1]] =
          c.putHash(
            cacheKey1,
            cacheHashKey1,
            testModel1,
            expireSeconds
          )

        val expectedErrorCode = ErrorCode.REDIS_COMMAND_ERROR

        await(actual) match {
          case -\/(e: NonFatalError) =>
            e.code must be(expectedErrorCode)
            e.underlying.isInstanceOf[RuntimeException] must be(true)
          case _ =>
            true must be(false) // テストを失敗させる
        }
        verify(rediscalaClient, times(1)).hset(any(), any(), any())(any())
        verify(rediscalaClient, times(1)).expire(any(), any())
      }

      "be throw `exception` when `expire` fatal error" in new SetUp {

        when(rediscalaClient.hset(Tag.unwrap(cacheKey1), Tag.unwrap(cacheHashKey1), testModel1)(fmt1)).thenReturn(
          Future.successful(true)
        )

        when(rediscalaClient.expire(Tag.unwrap(cacheKey1), expireSeconds.get)).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, TestModel1]] =
          c.putHash(
            cacheKey1,
            cacheHashKey1,
            testModel1,
            expireSeconds
          )

        val expected: \/[EsError, TestModel1] = \/-(testModel1)
        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).hset(any(), any(), any())(any())
        verify(rediscalaClient, times(1)).expire(any(), any())
      }

    }

    "putBulkHash" must {

      "be successful" in new SetUp {

        when(rediscalaClient.hmset(Tag.unwrap(cacheKey1), vMap.map(v => Tag.unwrap(v._1) -> v._2))).thenReturn(
          Future.successful(true)
        )

        when(rediscalaClient.expire(Tag.unwrap(cacheKey1), expireSeconds.get)).thenReturn(
          Future.successful(true)
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.putBulkHash(
            cacheKey1,
            vMap,
            expireSeconds
          )

        val expected: \/[EsError, Seq[TestModel1]] = \/-(Seq(testModel1, testModel1))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).hmset(any(), any())(any())
        verify(rediscalaClient, times(1)).expire(any(), any())
      }

      "be return `REDIS_COMMAND_ERROR` when `hmset` returned false" in new SetUp {

        when(rediscalaClient.hmset(Tag.unwrap(cacheKey1), vMap.map(v => Tag.unwrap(v._1) -> v._2))).thenReturn(
          Future.successful(false)
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.putBulkHash(
            cacheKey1,
            vMap,
            expireSeconds
          )

        val expectedErrorCode: ErrorCode = ErrorCode.REDIS_COMMAND_ERROR

        await(actual) match {
          case -\/(e: NonFatalError) =>
            e.code must be(expectedErrorCode)
            e.underlying.isInstanceOf[RuntimeException] must be(true)
          case _ =>
            true must be(false) // テストを失敗させる
        }
        verify(rediscalaClient, times(1)).hmset(any(), any())(any())
        verify(rediscalaClient, times(0)).expire(any(), any())
      }

      "be return `REDIS_REQUEST_ERROR` when `hmset` non-fatal error" in new SetUp {
        when(rediscalaClient.hmset(Tag.unwrap(cacheKey1), vMap.map(v => Tag.unwrap(v._1) -> v._2))).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.putBulkHash(
            cacheKey1,
            vMap,
            expireSeconds
          )

        val expected = -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).hmset(any(), any())(any())
        verify(rediscalaClient, times(0)).expire(any(), any())
      }

      "be throw `exception` when `hmset` fatal error" in new SetUp {
        when(rediscalaClient.hmset(Tag.unwrap(cacheKey1), vMap.map(v => Tag.unwrap(v._1) -> v._2))).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.putBulkHash(
            cacheKey1,
            vMap,
            expireSeconds
          )

        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).hmset(any(), any())(any())
        verify(rediscalaClient, times(0)).expire(any(), any())
      }

      "be return `REDIS_REQUEST_ERROR` when `expire` non-fatal error" in new SetUp {

        when(rediscalaClient.hmset(Tag.unwrap(cacheKey1), vMap.map(v => Tag.unwrap(v._1) -> v._2))).thenReturn(
          Future.successful(true)
        )

        when(rediscalaClient.expire(Tag.unwrap(cacheKey1), expireSeconds.get)).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.putBulkHash(
            cacheKey1,
            vMap,
            expireSeconds
          )

        val expected: \/[EsError, TestModel1] = -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).hmset(any(), any())(any())
        verify(rediscalaClient, times(1)).expire(any(), any())
      }

      "be return `REDIS_REQUEST_ERROR` when `expire` returned false" in new SetUp {

        when(rediscalaClient.hmset(Tag.unwrap(cacheKey1), vMap.map(v => Tag.unwrap(v._1) -> v._2))).thenReturn(
          Future.successful(true)
        )

        when(rediscalaClient.expire(Tag.unwrap(cacheKey1), expireSeconds.get)).thenReturn(
          Future.successful(false)
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.putBulkHash(
            cacheKey1,
            vMap,
            expireSeconds
          )

        val expectedErrorCode = ErrorCode.REDIS_COMMAND_ERROR

        await(actual) match {
          case -\/(e: NonFatalError) =>
            e.code must be(expectedErrorCode)
            e.underlying.isInstanceOf[RuntimeException] must be(true)
          case _ =>
            true must be(false) // テストを失敗させる
        }
        verify(rediscalaClient, times(1)).hmset(any(), any())(any())
        verify(rediscalaClient, times(1)).expire(any(), any())
      }

      "be throw `exception` when `expire` fatal error" in new SetUp {

        when(rediscalaClient.hmset(Tag.unwrap(cacheKey1), vMap.map(v => Tag.unwrap(v._1) -> v._2))).thenReturn(
          Future.successful(true)
        )

        when(rediscalaClient.expire(Tag.unwrap(cacheKey1), expireSeconds.get)).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.putBulkHash(
            cacheKey1,
            vMap,
            expireSeconds
          )

        val expected: \/[EsError, TestModel1] = \/-(testModel1)
        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).hmset(any(), any())(any())
        verify(rediscalaClient, times(1)).expire(any(), any())
      }

    }

    "scan" must {

      val cacheKeys = Seq("a", "b")

      "be successful when non-empty" in new SetUp {

        when(rediscalaClient.keys(Tag.unwrap(cacheKeyGlob))).thenReturn(
          Future.successful(cacheKeys)
        )

        when(rediscalaClient.mget[TestModel1](cacheKeys: _*)).thenReturn(
          Future.successful(Seq(Some(testModel1), Some(testModel1)))
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.scan(cacheKeyGlob)(fmt1)

        val expected: \/[EsError, Seq[TestModel1]] = Seq(testModel1, testModel1).right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).keys(any())
        verify(rediscalaClient, times(1)).mget(any())(any())
      }

      "be successful when empty" in new SetUp {
        when(rediscalaClient.keys(Tag.unwrap(cacheKeyGlob))).thenReturn(
          Future.successful(cacheKeys)
        )

        when(rediscalaClient.mget[TestModel1](cacheKeys: _*)).thenReturn(
          Future.successful(Seq())
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.scan(cacheKeyGlob)(fmt1)

        val expected: \/[EsError, Seq[TestModel1]] = Nil.right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).keys(any())
        verify(rediscalaClient, times(1)).mget(any())(any())
      }

      "be return `REDIS_REQUEST_ERROR` when `keys` non-fatal error" in new SetUp {
        when(rediscalaClient.keys(Tag.unwrap(cacheKeyGlob))).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.scan(cacheKeyGlob)(fmt1)

        val expected = -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).keys(any())
        verify(rediscalaClient, times(0)).mget(any())(any())
      }

      "be throw `exception` when `keys` fatal error" in new SetUp {
        when(rediscalaClient.keys(Tag.unwrap(cacheKeyGlob))).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.scan(cacheKeyGlob)(fmt1)

        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).keys(any())
        verify(rediscalaClient, times(0)).mget(any())(any())
      }

      "be return `REDIS_REQUEST_ERROR` when `mget` non-fatal error" in new SetUp {
        when(rediscalaClient.keys(Tag.unwrap(cacheKeyGlob))).thenReturn(
          Future.successful(cacheKeys)
        )

        when(rediscalaClient.mget[TestModel1](cacheKeys: _*)).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.scan(cacheKeyGlob)(fmt1)

        val expected = -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).keys(any())
        verify(rediscalaClient, times(1)).mget(any())(any())
      }

      "be throw `exception` when `mget` fatal error" in new SetUp {
        when(rediscalaClient.keys(Tag.unwrap(cacheKeyGlob))).thenReturn(
          Future.successful(cacheKeys)
        )

        when(rediscalaClient.mget[TestModel1](cacheKeys: _*)).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.scan(cacheKeyGlob)(fmt1)

        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).keys(any())
        verify(rediscalaClient, times(1)).mget(any())(any())
      }
    }

    "scanHash" must {

      "be successful when non empty" in new SetUp {
        when(
          rediscalaClient.hscan[TestModel1](Tag.unwrap(cacheKey1), 0, 10000.some, Tag.unwrap(cacheHashKeyGlob1).some)
        ).thenReturn(
          Future.successful(Cursor(0, vMap.map(x => Tag.unwrap(x._1) -> x._2)))
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.scanHash(cacheKey1, cacheHashKeyGlob1)(fmt1)

        val expected: \/[EsError, Seq[TestModel1]] = vMap.values.toList.right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).hscan(any(), any(), any(), any())(any())
      }

      "be successful when empty" in new SetUp {
        when(
          rediscalaClient.hscan[TestModel1](Tag.unwrap(cacheKey1), 0, 10000.some, Tag.unwrap(cacheHashKeyGlob1).some)
        ).thenReturn(
          Future.successful(Cursor(0, Map.empty[String, TestModel1]))
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.scanHash(cacheKey1, cacheHashKeyGlob1)(fmt1)

        val expected: \/[EsError, Seq[TestModel1]] = Nil.right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).hscan(any(), any(), any(), any())(any())
      }

      "be return `REDIS_REQUEST_ERROR` when `hscan` non-fatal error" in new SetUp {
        when(
          rediscalaClient.hscan[TestModel1](Tag.unwrap(cacheKey1), 0, 10000.some, Tag.unwrap(cacheHashKeyGlob1).some)
        ).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.scanHash(cacheKey1, cacheHashKeyGlob1)(fmt1)

        val expected = -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).hscan(any(), any(), any(), any())(any())
      }

      "be throw `exception` when `hscan` fatal error" in new SetUp {
        when(
          rediscalaClient.hscan[TestModel1](Tag.unwrap(cacheKey1), 0, 10000.some, Tag.unwrap(cacheHashKeyGlob1).some)
        ).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.scanHash(cacheKey1, cacheHashKeyGlob1)(fmt1)

        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).hscan(any(), any(), any(), any())(any())
      }
    }

    "get" must {

      "be successful when non-empty" in new SetUp {
        when(rediscalaClient.get(Tag.unwrap(cacheKey1))(fmt1)).thenReturn(
          Future.successful(testModel1.some)
        )

        val actual: Task[\/[EsError, Option[TestModel1]]] =
          c.get(CacheKey(Tag.unwrap(cacheKey1)))(fmt1)

        val expected: \/[EsError, Option[TestModel1]] = Some(testModel1).right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).get(any())(any())
      }

      "be successful when empty" in new SetUp {
        when(rediscalaClient.get[TestModel1](Tag.unwrap(cacheKey1))(fmt1)).thenReturn(
          Future.successful(None)
        )

        val actual: Task[\/[EsError, Option[TestModel1]]] =
          c.get(CacheKey(Tag.unwrap(cacheKey1)))(fmt1)

        val expected: \/[EsError, Option[TestModel1]] = None.right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).get(any())(any())
      }

      "be return `REDIS_REQUEST_ERROR` when `get` non-fatal error" in new SetUp {
        when(rediscalaClient.get(Tag.unwrap(cacheKey1))(fmt1)).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, Option[TestModel1]]] =
          c.get(CacheKey(Tag.unwrap(cacheKey1)))(fmt1)

        val expected = -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).get(any())(any())
      }

      "be throw `exception` when `get` fatal error" in new SetUp {
        when(rediscalaClient.get(Tag.unwrap(cacheKey1))(fmt1)).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, Option[TestModel1]]] =
          c.get(CacheKey(Tag.unwrap(cacheKey1)))(fmt1)

        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).get(any())(any())
      }
    }

    "getList" must {

      "be successful when non-empty" in new SetUp {
        when(rediscalaClient.lrange(Tag.unwrap(cacheKey1), 0, -1)(fmt1)).thenReturn(
          Future.successful(Seq(testModel1, testModel1))
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.getList(CacheKey(Tag.unwrap(cacheKey1)))(fmt1)

        val expected: \/[EsError, Seq[TestModel1]] = Seq(testModel1, testModel1).right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).lrange(any(), any(), any())(any())
      }

      "be successful when empty" in new SetUp {
        when(rediscalaClient.lrange(Tag.unwrap(cacheKey1), 0, -1)(fmt1)).thenReturn(
          Future.successful(Seq.empty[TestModel1])
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.getList(CacheKey(Tag.unwrap(cacheKey1)))(fmt1)

        val expected: \/[EsError, Seq[TestModel1]] = Nil.right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).lrange(any(), any(), any())(any())
      }

      "be return `REDIS_REQUEST_ERROR` when `lrange` non-fatal error" in new SetUp {
        when(rediscalaClient.lrange(Tag.unwrap(cacheKey1), 0, -1)(fmt1)).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.getList(CacheKey(Tag.unwrap(cacheKey1)))(fmt1)

        val expected = -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).lrange(any(), any(), any())(any())
      }

      "be throw `exception` when `lrange` fatal error" in new SetUp {
        when(rediscalaClient.lrange(Tag.unwrap(cacheKey1), 0, -1)(fmt1)).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.getList(CacheKey(Tag.unwrap(cacheKey1)))(fmt1)

        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).lrange(any(), any(), any())(any())
      }
    }

    "getAllHash" must {

      "be successful when non empty" in new SetUp {
        when(
          rediscalaClient.hgetall[TestModel1](Tag.unwrap(cacheKey1))
        ).thenReturn(
          Future.successful(vMap.map(x => Tag.unwrap(x._1) -> x._2))
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.getAllHash(cacheKey1)(fmt1)

        val expected: \/[EsError, Seq[TestModel1]] = vMap.values.toList.right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).hgetall(any())(any())
      }

      "be successful when empty" in new SetUp {
        when(
          rediscalaClient.hgetall[TestModel1](Tag.unwrap(cacheKey1))
        ).thenReturn(
          Future.successful(Map.empty[String, TestModel1])
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.getAllHash(cacheKey1)(fmt1)

        val expected: \/[EsError, Seq[TestModel1]] = Nil.right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).hgetall(any())(any())
      }

      "be return `REDIS_REQUEST_ERROR` when `hscan` non-fatal error" in new SetUp {
        when(
          rediscalaClient.hgetall[TestModel1](Tag.unwrap(cacheKey1))
        ).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.getAllHash(cacheKey1)(fmt1)

        val expected = -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).hgetall(any())(any())
      }

      "be throw `exception` when `hscan` fatal error" in new SetUp {
        when(
          rediscalaClient.hgetall[TestModel1](Tag.unwrap(cacheKey1))
        ).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, Seq[TestModel1]]] =
          c.getAllHash(cacheKey1)(fmt1)

        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).hgetall(any())(any())
      }
    }

    "delete" must {

      "be successful when exists" in new SetUp {
        when(rediscalaClient.del(Tag.unwrap(cacheKey1))).thenReturn(
          Future.successful(1L)
        )

        val actual: Task[\/[EsError, Unit]] =
          c.delete(cacheKey1)

        val expected: \/[EsError, Unit] = ().right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).del(any())
      }

      "be successful when not exists" in new SetUp {
        when(rediscalaClient.del(Tag.unwrap(cacheKey1))).thenReturn(
          Future.successful(0L)
        )

        val actual: Task[\/[EsError, Unit]] =
          c.delete(cacheKey1)

        val expected: \/[EsError, Unit] = ().right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).del(any())
      }

      "be return `REDIS_REQUEST_ERROR` when `del` non-fatal error" in new SetUp {
        when(rediscalaClient.del(Tag.unwrap(cacheKey1))).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, Unit]] =
          c.delete(cacheKey1)

        val expected = -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).del(any())
      }

      "be throw `exception` when `del` fatal error" in new SetUp {
        when(rediscalaClient.del(Tag.unwrap(cacheKey1))).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, Unit]] =
          c.delete(cacheKey1)

        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).del(any())
      }
    }

    "deleteHash" must {

      "be successful when exists" in new SetUp {
        val cacheHashKeySeq = Seq(cacheHashKey1, cacheHashKey2)
        when(rediscalaClient.hdel(Tag.unwrap(cacheKey1), cacheHashKeySeq.map(Tag.unwrap): _*)).thenReturn(
          Future.successful(2L)
        )

        val actual: Task[\/[EsError, Unit]] =
          c.deleteHash(cacheKey1, cacheHashKeySeq)

        val expected: \/[EsError, Unit] = ().right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).hdel(any(), any())
      }

      "be successful when not exists" in new SetUp {
        val cacheHashKeySeq = Seq(cacheHashKey1, cacheHashKey2)
        when(rediscalaClient.hdel(Tag.unwrap(cacheKey1), cacheHashKeySeq.map(Tag.unwrap): _*)).thenReturn(
          Future.successful(0L)
        )

        val actual: Task[\/[EsError, Unit]] =
          c.deleteHash(cacheKey1, cacheHashKeySeq)

        val expected: \/[EsError, Unit] = ().right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).hdel(any(), any())
      }

      "be return `REDIS_REQUEST_ERROR` when `del` non-fatal error" in new SetUp {
        val cacheHashKeySeq = Seq(cacheHashKey1, cacheHashKey2)
        when(rediscalaClient.hdel(Tag.unwrap(cacheKey1), cacheHashKeySeq.map(Tag.unwrap): _*)).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, Unit]] =
          c.deleteHash(cacheKey1, cacheHashKeySeq)

        val expected = -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).hdel(any(), any())
      }

      "be throw `exception` when `del` fatal error" in new SetUp {
        val cacheHashKeySeq = Seq(cacheHashKey1, cacheHashKey2)
        when(rediscalaClient.hdel(Tag.unwrap(cacheKey1), cacheHashKeySeq.map(Tag.unwrap): _*)).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, Unit]] =
          c.deleteHash(cacheKey1, cacheHashKeySeq)

        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).hdel(any(), any())
      }
    }

    "exists" must {

      "be successful when exists" in new SetUp {
        when(rediscalaClient.exists(Tag.unwrap(cacheKey1))).thenReturn(
          Future.successful(true)
        )

        val actual: Task[\/[EsError, Boolean]] =
          c.exists(CacheKey(Tag.unwrap(cacheKey1)))

        val expected: \/[EsError, Boolean] = true.right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).exists(any())
      }

      "be successful when not exists" in new SetUp {
        when(rediscalaClient.exists(Tag.unwrap(cacheKey1))).thenReturn(
          Future.successful(false)
        )

        val actual: Task[\/[EsError, Boolean]] =
          c.exists(CacheKey(Tag.unwrap(cacheKey1)))

        val expected: \/[EsError, Boolean] = false.right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).exists(any())
      }

      "be return `REDIS_REQUEST_ERROR` when `exists` non-fatal error" in new SetUp {
        when(rediscalaClient.exists(Tag.unwrap(cacheKey1))).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, Boolean]] =
          c.exists(CacheKey(Tag.unwrap(cacheKey1)))

        val expected = -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).exists(any())
      }

      "be throw `exception` when `exists` fatal error" in new SetUp {
        when(rediscalaClient.exists(Tag.unwrap(cacheKey1))).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, Boolean]] =
          c.exists(CacheKey(Tag.unwrap(cacheKey1)))

        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).exists(any())
      }
    }

    "clear" must {

      "be successful" in new SetUp {
        when(rediscalaClient.flushdb()).thenReturn(
          Future.successful(true)
        )

        val actual: Task[\/[EsError, Unit]] =
          c.clear

        val expected: \/[EsError, Unit] = ().right[EsError]
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).flushdb()
      }

      "be return `REDIS_REQUEST_ERROR` when `flushdb` non-fatal error" in new SetUp {
        when(rediscalaClient.flushdb()).thenThrow(
          nonFatalException
        )

        val actual: Task[\/[EsError, Unit]] =
          c.clear

        val expected = -\/(NonFatalError(nonFatalException, ErrorCode.REDIS_REQUEST_ERROR))
        await(actual) must be(expected)
        verify(rediscalaClient, times(1)).flushdb()
      }

      "be return `REDIS_COMMAND_ERROR` when `flushdb` returned false" in new SetUp {
        when(rediscalaClient.flushdb()).thenReturn(
          Future.successful(false)
        )

        val actual: Task[\/[EsError, Unit]] =
          c.clear

        val expectedErrorCode: ErrorCode = ErrorCode.REDIS_COMMAND_ERROR

        await(actual) match {
          case -\/(e: NonFatalError) =>
            e.code must be(expectedErrorCode)
            e.underlying.isInstanceOf[RuntimeException] must be(true)
          case _ =>
            true must be(false) // テストを失敗させる
        }

        verify(rediscalaClient, times(1)).flushdb()
      }

      "be throw `exception` when `flushdb` fatal error" in new SetUp {
        when(rediscalaClient.flushdb()).thenThrow(
          fatalException
        )

        val actual: Task[\/[EsError, Unit]] =
          c.clear

        intercept[OutOfMemoryError] {
          await(actual)
        }
        verify(rediscalaClient, times(1)).flushdb()
      }

    }
  }

}

object RedisClientImplSpec {
  case class TestId1(value: String) extends Identifier[String]
  object TestId1 extends CacheableId[TestId1] {
    override implicit val encoder: Encoder[TestId1] = deriveIdentifierEncoder
    override implicit val decoder: Decoder[TestId1] = deriveIdentifierDecoder
  }

  case class TestVO1(value: String) extends ValueObject
  object TestVO1 extends CacheableValueObject[TestVO1] {
    override implicit val encoder: Encoder[TestVO1] = deriveValueObjectEncoder
    override implicit val decoder: Decoder[TestVO1] = deriveValueObjectDecoder
  }

  case class TestModel1(id: TestId1, v1: Int, v2: Option[String], v3: TestVO1) {
    val cacheKey: String = id.value
  }
  object TestModel1 extends CacheableModel[TestModel1] {
    import io.circe.generic.semiauto._
    override implicit val encoder: Encoder[TestModel1] = deriveEncoder
    override implicit val decoder: Decoder[TestModel1] = deriveDecoder
    val dbNum: Int                                     = 1
  }

  case class TestId2(value: String) extends Identifier[String]
  object TestId2 extends CacheableId[TestId2] {
    override implicit val encoder: Encoder[TestId2] = deriveIdentifierEncoder
    override implicit val decoder: Decoder[TestId2] = deriveIdentifierDecoder
  }

  case class TestVO2(value: String) extends ValueObject
  object TestVO2 extends CacheableValueObject[TestVO2] {
    override implicit val encoder: Encoder[TestVO2] = deriveValueObjectEncoder
    override implicit val decoder: Decoder[TestVO2] = deriveValueObjectDecoder
  }

  case class TestModel2(id: TestId2, v1: Int, v2: Option[String], v3: TestVO2) {
    val cacheKey: String = id.value
  }
  object TestModel2 extends CacheableModel[TestModel2] {
    import io.circe.generic.semiauto._
    override implicit val encoder: Encoder[TestModel2] = deriveEncoder
    override implicit val decoder: Decoder[TestModel2] = deriveDecoder
    val dbNum: Int                                     = 2
  }

}
