package jp.eigosapuri.es.shared.adapter.secondary.eff.cache

import org.atnos.eff._
import org.atnos.eff.addon.monix.task._
import org.atnos.eff.syntax.addon.monix.task._
import org.atnos.eff.syntax.addon.scalaz.either._
import org.atnos.eff.syntax.all._
import org.mockito.{ Matchers, Mockito }

import io.circe.{ Decoder, Encoder }
import monix.eval.Task
import monix.execution.CancelableFuture
import redis.ByteStringFormatter

import scalaz.Scalaz._
import scalaz._

import jp.eigosapuri.es.shared.adapter.secondary.eff.cache.interpreter.{ CacheIOInterpreter, CacheIOInterpreterImpl }
import jp.eigosapuri.es.shared.domain.log.LogMessage
import jp.eigosapuri.es.shared.lib.dddSupport.EsError.NonFatalError
import jp.eigosapuri.es.shared.lib.dddSupport.domain.cache.{ CacheableId, CacheableModel, CacheableValueObject }
import jp.eigosapuri.es.shared.lib.dddSupport.domain.{ Identifier, ValueObject }
import jp.eigosapuri.es.shared.lib.dddSupport.{ ErrorCode, EsError }
import jp.eigosapuri.es.shared.lib.eff.cache.CacheIOTypes._
import jp.eigosapuri.es.shared.lib.eff.cache.{ CacheIO, RedisClient }
import jp.eigosapuri.es.shared.lib.eff.cache.RedisClient._
import jp.eigosapuri.es.shared.lib.test.{ AbstractSpecification, DeterministicTestObject }

class CacheIOEffectSpec extends AbstractSpecification {
  import Matchers._
  import Mockito._

  import monix.execution.Scheduler.Implicits.global

  import CacheIOEffectSpec._
  import jp.eigosapuri.es.shared.adapter.secondary.eff.cache.CacheIOEffect._
  import jp.eigosapuri.es.shared.lib.eff.either.EsErrorEffect._
  import jp.eigosapuri.es.shared.lib.eff._
  implicit val ec: ExecutorServices = org.atnos.eff.ExecutorServices.fromGlobalExecutionContext

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

    val c: RedisClient = Mockito.mock(classOf[RedisClient])
    // val c = new RedisClient("127.0.0.1", 6379, password = None)(akka.actor.ActorSystem("redis"))
    implicit val interpreter                           = new CacheIOInterpreterImpl(c)
    implicit val fmt1: ByteStringFormatter[TestModel1] = byteStringFormatter[TestModel1]
    implicit val fmt2: ByteStringFormatter[TestModel2] = byteStringFormatter[TestModel2]
    val expireSeconds: Option[Long]                    = 10L.some
    val testException                                  = new Exception("Redis Protocol error")
    val cacheKeyGlob: CacheKeyGlob                     = CacheKeyGlob("*")
  }

  "CacheIOEffect" should {

    "Put" must {

      "be successful" in new SetUp {
        when(c.put(testModel1.cacheKey, testModel1, expireSeconds)(fmt1)).thenReturn(
          Task.delay(testModel1.right[EsError])
        )

        val actual: CancelableFuture[\/[EsError, (TestModel1, List[LogMessage])]] =
          CacheIO
            .store[TestModel1, CacheIOStack](
              testModel1.cacheKey,
              testModel1,
              expireSeconds
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (TestModel1, List[LogMessage])] =
          \/-(
            (
              testModel1,
              List(
                LogMessage(s"Put(${testModel1.id.value}, $testModel1, $expireSeconds)")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).put(any(), any(), any())(any())
      }

      "be return `EsError` when non-fatal error" in new SetUp {
        when(c.put(testModel1.cacheKey, testModel1, expireSeconds)(fmt1)).thenReturn(
          Task.delay(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR).left)
        )

        val actual: CancelableFuture[\/[EsError, (TestModel1, List[LogMessage])]] =
          CacheIO
            .store[TestModel1, CacheIOStack](
              testModel1.cacheKey,
              testModel1,
              expireSeconds
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (TestModel1, List[LogMessage])] =
          -\/(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR))

        await(actual) must be(expected)
        verify(c, times(1)).put(any(), any(), any())(any())
      }

      "be throw exeception when fatal error" in new SetUp {
        when(c.put(testModel1.cacheKey, testModel1, expireSeconds)(fmt1)).thenReturn(
          Task.delay(throw testException)
        )

        val actual: CancelableFuture[\/[EsError, (TestModel1, List[LogMessage])]] =
          CacheIO
            .store[TestModel1, CacheIOStack](
              testModel1.cacheKey,
              testModel1,
              expireSeconds
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        an[Exception] should be thrownBy await(actual)
        verify(c, times(1)).put(any(), any(), any())(any())
      }
    }

    "PutList" must {

      "be successful" in new SetUp {
        val listKey = CacheKey("list")
        val values  = Seq(testModel1, testModel1)
        when(c.putList(listKey, values, expireSeconds)(fmt1)).thenReturn(
          Task.delay(values.right[EsError])
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .storeList[TestModel1, CacheIOStack](
              listKey,
              values,
              expireSeconds
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Seq[TestModel1], List[LogMessage])] =
          \/-(
            (
              values,
              List(
                LogMessage(s"PutList($listKey, $values, $expireSeconds)")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).putList(any(), any(), any())(any())
      }

      "be return `EsError` when non-fatal error" in new SetUp {
        val listKey = CacheKey("list")
        val values  = Seq(testModel1, testModel1)
        when(c.putList(listKey, values, expireSeconds)(fmt1)).thenReturn(
          Task.delay(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR).left)
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .storeList[TestModel1, CacheIOStack](
              listKey,
              values,
              expireSeconds
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Seq[TestModel1], List[LogMessage])] =
          -\/(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR))

        await(actual) must be(expected)
        verify(c, times(1)).putList(any(), any(), any())(any())
      }

      "be throw exeception when fatal error" in new SetUp {
        val listKey = CacheKey("list")
        val values  = Seq(testModel1, testModel1)
        when(c.putList(listKey, values, expireSeconds)(fmt1)).thenReturn(
          Task.delay(throw testException)
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .storeList[TestModel1, CacheIOStack](
              listKey,
              values,
              expireSeconds
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        an[Exception] should be thrownBy await(actual)
        verify(c, times(1)).putList(any(), any(), any())(any())
      }

    }

    "PutHash" must {
      "be successful" in new SetUp {
        val key     = CacheKey("hash")
        val hashKey = CacheHashKey("hash_1")
        when(c.putHash(key, hashKey, testModel1, expireSeconds)(fmt1)).thenReturn(
          Task.delay(testModel1.right[EsError])
        )

        val actual: CancelableFuture[\/[EsError, (TestModel1, List[LogMessage])]] =
          CacheIO
            .storeHash[TestModel1, CacheIOStack](
              key,
              hashKey,
              testModel1,
              expireSeconds
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (TestModel1, List[LogMessage])] =
          \/-(
            (
              testModel1,
              List(
                LogMessage(s"PutHash($key, $hashKey, $testModel1, $expireSeconds)")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).putHash(any(), any(), any(), any())(any())
      }

      "be return `EsError` when non-fatal error" in new SetUp {
        val key     = CacheKey("hash")
        val hashKey = CacheHashKey("hash_1")
        when(c.putHash(key, hashKey, testModel1, expireSeconds)(fmt1)).thenReturn(
          Task.delay(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR).left)
        )

        val actual: CancelableFuture[\/[EsError, (TestModel1, List[LogMessage])]] =
          CacheIO
            .storeHash[TestModel1, CacheIOStack](
              key,
              hashKey,
              testModel1,
              expireSeconds
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Seq[TestModel1], List[LogMessage])] =
          -\/(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR))

        await(actual) must be(expected)
        verify(c, times(1)).putHash(any(), any(), any(), any())(any())
      }

      "be throw exeception when fatal error" in new SetUp {
        val key     = CacheKey("hash")
        val hashKey = CacheHashKey("hash_1")
        when(c.putHash(key, hashKey, testModel1, expireSeconds)(fmt1)).thenReturn(
          Task.delay(throw testException)
        )

        val actual: CancelableFuture[\/[EsError, (TestModel1, List[LogMessage])]] =
          CacheIO
            .storeHash[TestModel1, CacheIOStack](
              key,
              hashKey,
              testModel1,
              expireSeconds
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        an[Exception] should be thrownBy await(actual)
        verify(c, times(1)).putHash(any(), any(), any(), any())(any())
      }
    }

    "PutBulkHash" must {
      "be successful" in new SetUp {
        val key = CacheKey("hash")
        val values = Map(
          CacheHashKey("hash_1") -> testModel1,
          CacheHashKey("hash_2") -> testModel1
        )
        when(c.putBulkHash(key, values, expireSeconds)(fmt1)).thenReturn(
          Task.delay(Seq(testModel1, testModel1).right[EsError])
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .storeBulkHash[TestModel1, CacheIOStack](
              key,
              values,
              expireSeconds
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Seq[TestModel1], List[LogMessage])] =
          \/-(
            (
              Seq(testModel1, testModel1),
              List(
                LogMessage(s"PutBulkHash($key, $values, $expireSeconds)")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).putBulkHash(any(), any(), any())(any())
      }

      "be return `EsError` when non-fatal error" in new SetUp {
        val key = CacheKey("hash")
        val values = Map(
          CacheHashKey("hash_1") -> testModel1,
          CacheHashKey("hash_2") -> testModel1
        )
        when(c.putBulkHash(key, values, expireSeconds)(fmt1)).thenReturn(
          Task.delay(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR).left)
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .storeBulkHash[TestModel1, CacheIOStack](
              key,
              values,
              expireSeconds
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Seq[TestModel1], List[LogMessage])] =
          -\/(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR))

        await(actual) must be(expected)
        verify(c, times(1)).putBulkHash(any(), any(), any())(any())
      }

      "be throw exeception when fatal error" in new SetUp {
        val key = CacheKey("hash")
        val values = Map(
          CacheHashKey("hash_1") -> testModel1,
          CacheHashKey("hash_2") -> testModel1
        )
        when(c.putBulkHash(key, values, expireSeconds)(fmt1)).thenReturn(
          Task.delay(throw testException)
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .storeBulkHash[TestModel1, CacheIOStack](
              key,
              values,
              expireSeconds
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        an[Exception] should be thrownBy await(actual)
        verify(c, times(1)).putBulkHash(any(), any(), any())(any())
      }
    }

    "Get" must {

      "be successful" in new SetUp {
        when(c.get(testModel1.cacheKey)(fmt1)).thenReturn(
          Task.delay(testModel1.some.right[EsError])
        )

        val actual: CancelableFuture[\/[EsError, (Option[TestModel1], List[LogMessage])]] =
          CacheIO
            .find[TestModel1, CacheIOStack](
              testModel1.cacheKey
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Option[TestModel1], List[LogMessage])] =
          \/-(
            (
              Some(testModel1),
              List(
                LogMessage(s"Get(${testModel1.id.value})")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).get(any())(any())
      }

      "be return `EsError` when non-fatal error" in new SetUp {
        when(c.get(testModel1.cacheKey)(fmt1)).thenReturn(
          Task.delay(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR).left)
        )

        val actual: CancelableFuture[\/[EsError, (Option[TestModel1], List[LogMessage])]] =
          CacheIO
            .find[TestModel1, CacheIOStack](
              testModel1.cacheKey
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Option[TestModel1], List[LogMessage])] =
          -\/(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR))

        await(actual) must be(expected)
        verify(c, times(1)).get(any())(any())
      }

      "be throw exeception when fatal error" in new SetUp {
        when(c.get(testModel1.cacheKey)(fmt1)).thenReturn(
          Task.delay(throw testException)
        )

        val actual: CancelableFuture[\/[EsError, (Option[TestModel1], List[LogMessage])]] =
          CacheIO
            .find[TestModel1, CacheIOStack](
              testModel1.cacheKey
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        an[Exception] should be thrownBy await(actual)
        verify(c, times(1)).get(any())(any())
      }
    }

    "GetList" must {

      val key = CacheKey("list")

      "be successful" in new SetUp {
        when(c.getList(key)(fmt1)).thenReturn(
          Task.delay(Seq(testModel1, testModel1).right[EsError])
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .findList[TestModel1, CacheIOStack](
              key
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Seq[TestModel1], List[LogMessage])] =
          \/-(
            (
              Seq(testModel1, testModel1),
              List(
                LogMessage(s"GetList($key)")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).getList(any(), any(), any())(any())
      }

      "be return `EsError` when non-fatal error" in new SetUp {
        when(c.getList(key)(fmt1)).thenReturn(
          Task.delay(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR).left)
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .findList[TestModel1, CacheIOStack](
              key
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Seq[TestModel1], List[LogMessage])] =
          -\/(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR))

        await(actual) must be(expected)
        verify(c, times(1)).getList(any(), any(), any())(any())
      }

      "be throw exeception when fatal error" in new SetUp {
        when(c.getList(key)(fmt1)).thenReturn(
          Task.delay(throw testException)
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .findList[TestModel1, CacheIOStack](
              key
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        an[Exception] should be thrownBy await(actual)
        verify(c, times(1)).getList(any(), any(), any())(any())
      }

    }

    "GetAllHash" must {

      val key = CacheKey("hash")

      "be successful" in new SetUp {
        when(c.getAllHash(key)(fmt1)).thenReturn(
          Task.delay(Seq(testModel1, testModel1).right[EsError])
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .findAllHash[TestModel1, CacheIOStack](
              key
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Seq[TestModel1], List[LogMessage])] =
          \/-(
            (
              Seq(testModel1, testModel1),
              List(
                LogMessage(s"GetAllHash($key)")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).getAllHash(any())(any())
      }

      "be return `EsError` when non-fatal error" in new SetUp {
        when(c.getAllHash(key)(fmt1)).thenReturn(
          Task.delay(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR).left)
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .findAllHash[TestModel1, CacheIOStack](
              key
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Seq[TestModel1], List[LogMessage])] =
          -\/(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR))

        await(actual) must be(expected)
        verify(c, times(1)).getAllHash(any())(any())
      }

      "be throw exeception when fatal error" in new SetUp {
        when(c.getAllHash(key)(fmt1)).thenReturn(
          Task.delay(throw testException)
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .findAllHash[TestModel1, CacheIOStack](
              key
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        an[Exception] should be thrownBy await(actual)
        verify(c, times(1)).getAllHash(any())(any())
      }

    }

    "Scan" must {
      "be successful" in new SetUp {
        when(c.scan(cacheKeyGlob)(fmt1)).thenReturn(
          Task.delay(Seq(testModel1).right[EsError])
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .findAllByMatchGlob[TestModel1, CacheIOStack](
              cacheKeyGlob
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Seq[TestModel1], List[LogMessage])] =
          \/-(
            (
              Seq(testModel1),
              List(
                LogMessage(s"Scan($cacheKeyGlob)")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).scan(any())(any())
      }

      "be return `EsError` when non-fatal error" in new SetUp {
        when(c.scan(cacheKeyGlob)(fmt1)).thenReturn(
          Task.delay(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR).left)
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .findAllByMatchGlob[TestModel1, CacheIOStack](
              cacheKeyGlob
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Seq[TestModel1], List[LogMessage])] =
          -\/(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR))

        await(actual) must be(expected)
        verify(c, times(1)).scan(any())(any())
      }

      "be throw exeception when fatal error" in new SetUp {
        when(c.scan(cacheKeyGlob)(fmt1)).thenReturn(
          Task.delay(throw testException)
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .findAllByMatchGlob[TestModel1, CacheIOStack](
              cacheKeyGlob
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        an[Exception] should be thrownBy await(actual)
        verify(c, times(1)).scan(any())(any())
      }
    }

    "ScanOne" must {

      "be successful" in new SetUp {
        when(c.scan(cacheKeyGlob)(fmt1)).thenReturn(
          Task.delay(Seq(testModel1).right[EsError])
        )

        val actual: CancelableFuture[\/[EsError, (Option[TestModel1], List[LogMessage])]] =
          CacheIO
            .findByMatchGlob[TestModel1, CacheIOStack](
              cacheKeyGlob
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Option[TestModel1], List[LogMessage])] =
          \/-(
            (
              testModel1.some,
              List(
                LogMessage(s"ScanOne($cacheKeyGlob)")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).scan(any())(any())
      }

      "be return `EsError` when non-fatal error" in new SetUp {
        when(c.scan(cacheKeyGlob)(fmt1)).thenReturn(
          Task.delay(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR).left)
        )

        val actual: CancelableFuture[\/[EsError, (Option[TestModel1], List[LogMessage])]] =
          CacheIO
            .findByMatchGlob[TestModel1, CacheIOStack](
              cacheKeyGlob
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Option[TestModel1], List[LogMessage])] =
          -\/(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR))

        await(actual) must be(expected)
        verify(c, times(1)).scan(any())(any())
      }

      "be throw exeception when fatal error" in new SetUp {
        when(c.scan(cacheKeyGlob)(fmt1)).thenReturn(
          Task.delay(throw testException)
        )

        val actual: CancelableFuture[\/[EsError, (Option[TestModel1], List[LogMessage])]] =
          CacheIO
            .findByMatchGlob[TestModel1, CacheIOStack](
              cacheKeyGlob
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        an[Exception] should be thrownBy await(actual)
        verify(c, times(1)).scan(any())(any())
      }
    }

    "ScanHash" must {

      val key         = CacheKey("hash")
      val hashKeyGlob = CacheHashKeyGlob("hash_*")

      "be successful" in new SetUp {
        when(c.scanHash(key, hashKeyGlob)(fmt1)).thenReturn(
          Task.delay(Seq(testModel1, testModel1).right[EsError])
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .findHashAllByMatchGlob[TestModel1, CacheIOStack](
              key,
              hashKeyGlob
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Seq[TestModel1], List[LogMessage])] =
          \/-(
            (
              Seq(testModel1, testModel1),
              List(
                LogMessage(s"ScanHash($key, $hashKeyGlob)")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).scanHash(any(), any())(any())
      }

      "be return `EsError` when non-fatal error" in new SetUp {
        when(c.scanHash(key, hashKeyGlob)(fmt1)).thenReturn(
          Task.delay(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR).left)
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .findHashAllByMatchGlob[TestModel1, CacheIOStack](
              key,
              hashKeyGlob
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Seq[TestModel1], List[LogMessage])] =
          -\/(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR))

        await(actual) must be(expected)
        verify(c, times(1)).scanHash(any(), any())(any())
      }

      "be throw exeception when fatal error" in new SetUp {
        when(c.scanHash(key, hashKeyGlob)(fmt1)).thenReturn(
          Task.delay(throw testException)
        )

        val actual: CancelableFuture[\/[EsError, (Seq[TestModel1], List[LogMessage])]] =
          CacheIO
            .findHashAllByMatchGlob[TestModel1, CacheIOStack](
              key,
              hashKeyGlob
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        an[Exception] should be thrownBy await(actual)
        verify(c, times(1)).scanHash(any(), any())(any())
      }

    }

    "ScanHashOne" must {

      val key         = CacheKey("hash")
      val hashKeyGlob = CacheHashKeyGlob("hash_1")

      "be successful" in new SetUp {
        when(c.scanHash(key, hashKeyGlob)(fmt1)).thenReturn(
          Task.delay(Seq(testModel1).right[EsError])
        )

        val actual: CancelableFuture[\/[EsError, (Option[TestModel1], List[LogMessage])]] =
          CacheIO
            .findHashByMatchGlob[TestModel1, CacheIOStack](
              key,
              hashKeyGlob
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Option[TestModel1], List[LogMessage])] =
          \/-(
            (
              Some(testModel1),
              List(
                LogMessage(s"ScanHashOne($key, $hashKeyGlob)")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).scanHash(any(), any())(any())
      }

      "be successful when returned multiple value" in new SetUp {
        when(c.scanHash(key, hashKeyGlob)(fmt1)).thenReturn(
          Task.delay(Seq(testModel1, testModel1).right[EsError])
        )

        val actual: CancelableFuture[\/[EsError, (Option[TestModel1], List[LogMessage])]] =
          CacheIO
            .findHashByMatchGlob[TestModel1, CacheIOStack](
              key,
              hashKeyGlob
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Option[TestModel1], List[LogMessage])] =
          \/-(
            (
              Some(testModel1),
              List(
                LogMessage(s"ScanHashOne($key, $hashKeyGlob)")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).scanHash(any(), any())(any())
      }

      "be return `EsError` when non-fatal error" in new SetUp {
        when(c.scanHash(key, hashKeyGlob)(fmt1)).thenReturn(
          Task.delay(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR).left)
        )

        val actual: CancelableFuture[\/[EsError, (Option[TestModel1], List[LogMessage])]] =
          CacheIO
            .findHashByMatchGlob[TestModel1, CacheIOStack](
              key,
              hashKeyGlob
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Seq[TestModel1], List[LogMessage])] =
          -\/(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR))

        await(actual) must be(expected)
        verify(c, times(1)).scanHash(any(), any())(any())
      }

      "be throw exeception when fatal error" in new SetUp {
        when(c.scanHash(key, hashKeyGlob)(fmt1)).thenReturn(
          Task.delay(throw testException)
        )

        val actual: CancelableFuture[\/[EsError, (Option[TestModel1], List[LogMessage])]] =
          CacheIO
            .findHashByMatchGlob[TestModel1, CacheIOStack](
              key,
              hashKeyGlob
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        an[Exception] should be thrownBy await(actual)
        verify(c, times(1)).scanHash(any(), any())(any())
      }

    }

    "Delete" must {

      "be successful" in new SetUp {
        when(c.delete(testModel1.cacheKey)).thenReturn(
          Task.delay(().right[EsError])
        )

        val actual: CancelableFuture[\/[EsError, (Unit, List[LogMessage])]] =
          CacheIO
            .delete[CacheIOStack](
              testModel1.cacheKey
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Unit, List[LogMessage])] =
          \/-(
            (
              Seq(testModel1),
              List(
                LogMessage(s"Delete(${testModel1.id.value})")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).delete(any())
      }

      "be return `EsError` when non-fatal error" in new SetUp {
        when(c.delete(testModel1.cacheKey)).thenReturn(
          Task.delay(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR).left)
        )

        val actual: CancelableFuture[\/[EsError, (Unit, List[LogMessage])]] =
          CacheIO
            .delete[CacheIOStack](
              testModel1.cacheKey
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Unit, List[LogMessage])] =
          -\/(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR))

        await(actual) must be(expected)
        verify(c, times(1)).delete(any())
      }

      "be throw exeception when fatal error" in new SetUp {
        when(c.delete(testModel1.cacheKey)).thenReturn(
          Task.delay(throw testException)
        )

        val actual: CancelableFuture[\/[EsError, (Unit, List[LogMessage])]] =
          CacheIO
            .delete[CacheIOStack](
              testModel1.cacheKey
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        an[Exception] should be thrownBy await(actual)
        verify(c, times(1)).delete(any())
      }
    }

    "DeleteHash" must {

      val key      = CacheKey("hash")
      val hashKey1 = CacheHashKey("hash_1")
      val hashKey2 = CacheHashKey("hash_2")
      val hashKeys = Seq(hashKey1, hashKey2)

      "be successful" in new SetUp {
        when(c.deleteHash(key, hashKeys)).thenReturn(
          Task.delay(().right[EsError])
        )

        val actual: CancelableFuture[\/[EsError, (Unit, List[LogMessage])]] =
          CacheIO
            .deleteHash[CacheIOStack](
              key,
              hashKeys
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Unit, List[LogMessage])] =
          \/-(
            (
              (),
              List(
                LogMessage(s"DeleteHash($key, $hashKeys)")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).deleteHash(any(), any())
      }

      "be return `EsError` when non-fatal error" in new SetUp {
        when(c.deleteHash(key, hashKeys)).thenReturn(
          Task.delay(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR).left)
        )

        val actual: CancelableFuture[\/[EsError, (Unit, List[LogMessage])]] =
          CacheIO
            .deleteHash[CacheIOStack](
              key,
              hashKeys
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Unit, List[LogMessage])] =
          -\/(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR))

        await(actual) must be(expected)
        verify(c, times(1)).deleteHash(any(), any())
      }

      "be throw exeception when fatal error" in new SetUp {
        when(c.deleteHash(key, hashKeys)).thenReturn(
          Task.delay(throw testException)
        )

        val actual: CancelableFuture[\/[EsError, (Unit, List[LogMessage])]] =
          CacheIO
            .deleteHash[CacheIOStack](
              key,
              hashKeys
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        an[Exception] should be thrownBy await(actual)
        verify(c, times(1)).deleteHash(any(), any())
      }

    }

    "Has" must {

      "be successful" in new SetUp {
        when(c.exists(testModel1.cacheKey)).thenReturn(
          Task.delay(true.right[EsError])
        )

        val actual: CancelableFuture[\/[EsError, (Boolean, List[LogMessage])]] =
          CacheIO
            .exists[CacheIOStack](
              testModel1.cacheKey
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Boolean, List[LogMessage])] =
          \/-(
            (
              true,
              List(
                LogMessage(s"Has(${testModel1.id.value})")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).exists(any())
      }

      "be return `EsError` when non-fatal error" in new SetUp {
        when(c.exists(testModel1.cacheKey)).thenReturn(
          Task.delay(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR).left)
        )

        val actual: CancelableFuture[\/[EsError, (Boolean, List[LogMessage])]] =
          CacheIO
            .exists[CacheIOStack](
              testModel1.cacheKey
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Boolean, List[LogMessage])] =
          -\/(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR))

        await(actual) must be(expected)
        verify(c, times(1)).exists(any())
      }

      "be throw exeception when fatal error" in new SetUp {
        when(c.exists(testModel1.cacheKey)).thenReturn(
          Task.delay(throw testException)
        )

        val actual: CancelableFuture[\/[EsError, (Boolean, List[LogMessage])]] =
          CacheIO
            .exists[CacheIOStack](
              testModel1.cacheKey
            )
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        an[Exception] should be thrownBy await(actual)
        verify(c, times(1)).exists(any())
      }
    }

    "Clear" must {

      "be successful" in new SetUp {
        when(c.clear).thenReturn(
          Task.delay(().right[EsError])
        )

        val actual: CancelableFuture[\/[EsError, (Unit, List[LogMessage])]] =
          CacheIO
            .clear[CacheIOStack]
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Unit, List[LogMessage])] =
          \/-(
            (
              Seq(testModel1),
              List(
                LogMessage(s"Clear")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).clear
      }

      "be return `EsError` when non-fatal error" in new SetUp {
        when(c.clear).thenReturn(
          Task.delay(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR).left)
        )

        val actual: CancelableFuture[\/[EsError, (Unit, List[LogMessage])]] =
          CacheIO
            .clear[CacheIOStack]
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        val expected: \/[EsError, (Unit, List[LogMessage])] =
          -\/(NonFatalError(testException, ErrorCode.REDIS_REQUEST_ERROR))

        await(actual) must be(expected)
        verify(c, times(1)).clear
      }

      "be throw exeception when fatal error" in new SetUp {
        when(c.clear).thenReturn(
          Task.delay(throw testException)
        )

        val actual: CancelableFuture[\/[EsError, (Unit, List[LogMessage])]] =
          CacheIO
            .clear[CacheIOStack]
            .runCacheIO
            .runWriter
            .runEsError
            .runAsync
            .runAsync

        an[Exception] should be thrownBy await(actual)
        verify(c, times(1)).clear
      }
    }

    "combined" must {
      "be successful" in new SetUp {
        when(c.clear).thenReturn(
          Task.delay(().right[EsError])
        )
        when(c.put(testModel1.cacheKey, testModel1, expireSeconds)(fmt1)).thenReturn(
          Task.delay(testModel1.right[EsError])
        )
        when(c.exists(testModel1.cacheKey)).thenReturn(
          Task.delay(true.right[EsError])
        )
        when(c.get[TestModel1](testModel1.cacheKey)(fmt1)).thenReturn(
          Task.delay(testModel1.some.right[EsError])
        )
        when(c.scan(cacheKeyGlob)(fmt1)).thenReturn(
          Task.delay(Seq(testModel1).right[EsError])
        )
        when(c.delete(testModel1.cacheKey)).thenReturn(
          Task.delay(().right[EsError])
        )

        def program[R: _cacheio]: Eff[R, Option[TestModel1]] = {
          for {
            a <- CacheIO.clear
            b <- CacheIO.store[TestModel1, R](testModel1.cacheKey, testModel1, expireSeconds)
            c <- CacheIO.exists(testModel1.cacheKey)
            d <- CacheIO.find[TestModel1, R](testModel1.cacheKey)
            e <- CacheIO.findAllByMatchGlob[TestModel1, R](cacheKeyGlob)
            f <- CacheIO.delete(testModel1.cacheKey)
          } yield {
            a mustEqual Unit.unbox(Unit)
            b mustEqual testModel1
            c mustEqual true
            d mustEqual testModel1.some
            e mustEqual Seq(testModel1)
            f mustEqual Unit.unbox(Unit)
            d
          }
        }

        val actual: CancelableFuture[\/[EsError, (Option[TestModel1], List[LogMessage])]] =
          program[CacheIOStack].runCacheIO.runWriter.runEsError.runAsync.runAsync

        val expected: \/[EsError, (Option[TestModel1], List[LogMessage])] =
          \/-(
            (
              Some(testModel1),
              List(
                LogMessage("Clear"),
                LogMessage(s"Put(${testModel1.id.value}, $testModel1, $expireSeconds)"),
                LogMessage(s"Has(${testModel1.id.value})"),
                LogMessage(s"Get(${testModel1.id.value})"),
                LogMessage(s"Scan(${cacheKeyGlob})"),
                LogMessage(s"Delete(${testModel1.id.value})")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(1)).clear
        verify(c, times(1)).put(any(), any(), any())(any())
        verify(c, times(1)).exists(any())
        verify(c, times(1)).get(any())(any())
        verify(c, times(1)).scan(any())(any())
        verify(c, times(1)).delete(any())
      }

      "be successful when even it has different model" in new SetUp {
        when(c.clear).thenReturn(
          Task.delay(\/-(()))
        )
        when(c.put(testModel1.cacheKey, testModel1, expireSeconds)(fmt1)).thenReturn(
          Task.delay(\/-(testModel1))
        )
        when(c.exists(testModel1.cacheKey)).thenReturn(
          Task.delay(\/-(true))
        )
        when(c.get[TestModel1](testModel1.cacheKey)(fmt1)).thenReturn(
          Task.delay(\/-(testModel1.some))
        )
        when(c.scan(cacheKeyGlob)(fmt1)).thenReturn(
          Task.delay(Seq(testModel1).right[EsError])
        )
        when(c.delete(testModel1.cacheKey)).thenReturn(
          Task.delay(\/-(()))
        )

        def program1[R: _cacheio]: Eff[R, Unit] = {
          for {
            a <- CacheIO.clear
            b <- CacheIO.store[TestModel1, R](testModel1.cacheKey, testModel1, expireSeconds)
            c <- CacheIO.exists(testModel1.cacheKey)
            d <- CacheIO.find[TestModel1, R](testModel1.cacheKey)
            e <- CacheIO.findAllByMatchGlob[TestModel1, R](cacheKeyGlob)
            f <- CacheIO.delete(testModel1.cacheKey)
          } yield {
            a mustEqual Unit.unbox(Unit)
            b mustEqual testModel1
            c mustEqual true
            d mustEqual testModel1.some
            e mustEqual Seq(testModel1)
            f mustEqual Unit.unbox(Unit)
          }
        }

        when(c.clear).thenReturn(
          Task.delay(\/-(()))
        )
        when(c.put(testModel2.cacheKey, testModel2, expireSeconds)(fmt2)).thenReturn(
          Task.delay(\/-(testModel2))
        )
        when(c.exists(testModel2.cacheKey)).thenReturn(
          Task.delay(\/-(true))
        )
        when(c.get[TestModel2](testModel2.cacheKey)(fmt2)).thenReturn(
          Task.delay(\/-(testModel2.some))
        )
        when(c.scan(cacheKeyGlob)(fmt2)).thenReturn(
          Task.delay(Seq(testModel2).right[EsError])
        )
        when(c.delete(testModel2.cacheKey)).thenReturn(
          Task.delay(\/-(()))
        )

        def program2[R: _cacheio]: Eff[R, Option[TestModel2]] = {
          for {
            a <- CacheIO.clear
            b <- CacheIO.store[TestModel2, R](CacheKey(testModel2.id.value), testModel2, expireSeconds)
            c <- CacheIO.exists(testModel2.cacheKey)
            d <- CacheIO.find[TestModel2, R](CacheKey(testModel2.id.value))
            e <- CacheIO.findAllByMatchGlob[TestModel2, R](cacheKeyGlob)
            f <- CacheIO.delete(testModel2.cacheKey)
          } yield {
            a mustEqual Unit.unbox(Unit)
            b mustEqual testModel2
            c mustEqual true
            d mustEqual testModel2.some
            e mustEqual Seq(testModel2)
            f mustEqual Unit.unbox(Unit)
            d
          }
        }

        def combined[R: _cacheio]: Eff[R, Option[TestModel2]] =
          for {
            _ <- program1[R]
            b <- program2[R]
          } yield b

        val actual: CancelableFuture[\/[EsError, (Option[TestModel2], List[LogMessage])]] =
          combined[CacheIOStack].runCacheIO.runWriter.runEsError.runAsync.runAsync

        val expected: \/[EsError, (Option[TestModel2], List[LogMessage])] =
          \/-(
            (
              Some(testModel2),
              List(
                LogMessage("Clear"),
                LogMessage(s"Put(${testModel1.id.value}, $testModel1, $expireSeconds)"),
                LogMessage(s"Has(${testModel1.id.value})"),
                LogMessage(s"Get(${testModel1.id.value})"),
                LogMessage(s"Scan(${cacheKeyGlob})"),
                LogMessage(s"Delete(${testModel1.id.value})"),
                LogMessage("Clear"),
                LogMessage(s"Put(${testModel2.id.value}, $testModel2, $expireSeconds)"),
                LogMessage(s"Has(${testModel2.id.value})"),
                LogMessage(s"Get(${testModel2.id.value})"),
                LogMessage(s"Scan(${cacheKeyGlob})"),
                LogMessage(s"Delete(${testModel2.id.value})")
              )
            )
          )

        await(actual) must be(expected)
        verify(c, times(2)).clear
        verify(c, times(2)).put(any(), any(), any())(any())
        verify(c, times(2)).exists(any())
        verify(c, times(2)).get(any())(any())
        verify(c, times(2)).scan(any())(any())
        verify(c, times(2)).delete(any())
      }

      "be successful when even it has different effects" in new SetUp {
        when(c.clear).thenReturn(
          Task.delay(\/-(()))
        )
        when(c.put(testModel1.cacheKey, testModel1, expireSeconds)(fmt1)).thenReturn(
          Task.delay(\/-(testModel1))
        )
        when(c.exists(testModel1.cacheKey)).thenReturn(
          Task.delay(\/-(true))
        )
        when(c.get[TestModel1](testModel1.cacheKey)(fmt1)).thenReturn(
          Task.delay(\/-(testModel1.some))
        )
        when(c.scan(cacheKeyGlob)(fmt1)).thenReturn(
          Task.delay(Seq(testModel1).right[EsError])
        )
        when(c.delete(testModel1.cacheKey)).thenReturn(
          Task.delay(\/-(()))
        )

        def program[R: _cacheio: _task: _errorEither: ListCreation._list]: Eff[R, Option[TestModel1]] = {
          for {
            a <- CacheIO.clear
            b <- CacheIO.store[TestModel1, R](testModel1.cacheKey, testModel1, expireSeconds)
            c <- CacheIO.exists(testModel1.cacheKey)
            d <- CacheIO.find[TestModel1, R](testModel1.cacheKey)
            e <- CacheIO.findAllByMatchGlob[TestModel1, R](cacheKeyGlob)
            f <- CacheIO.delete(testModel1.cacheKey)
            g <- ListCreation.values(List(1, 2, 3): _*)
            h <- fromEsError(\/-("hogehoge"))
            i <- fromTask(Task.delay(testModel1))
          } yield {
            a must be(Unit.unbox(Unit))
            b must be(testModel1)
            c must be(true)
            d must be(testModel1.some)
            e must be(Seq(testModel1))
            f must be(Unit.unbox(Unit))
            //          g must (be(1) or be(2) or be(3))
            h must be("hogehoge")
            i must be(testModel1)
            d
          }
        }

        type Stack = FxAppend[CacheIOStack, Fx.fx1[List]]
        val actual: CancelableFuture[List[\/[EsError, (Option[TestModel1], List[LogMessage])]]] =
          program[Stack].runCacheIO.runWriter // _cacheio & _readerRedisClient
          .runEsError // _errorEither
          .runList // _list
          .runAsync // _test
          .runAsync // Task

        val expectedBase: \/[EsError, (Option[TestModel1], List[LogMessage])] = \/-(
          (
            Some(testModel1),
            List(
              LogMessage("Clear"),
              LogMessage(s"Put(${testModel1.id.value}, $testModel1, $expireSeconds)"),
              LogMessage(s"Has(${testModel1.id.value})"),
              LogMessage(s"Get(${testModel1.id.value})"),
              LogMessage(s"Scan($cacheKeyGlob)"),
              LogMessage(s"Delete(${testModel1.id.value})")
            )
          )
        )

        val expected: List[\/[EsError, (Option[TestModel1], List[LogMessage])]] =
          List(expectedBase, expectedBase, expectedBase)

        await(actual) must be(expected)
        verify(c, times(1)).clear
        verify(c, times(1)).put(any(), any(), any())(any())
        verify(c, times(1)).exists(any())
        verify(c, times(1)).get(any())(any())
        verify(c, times(1)).scan(any())(any())
        verify(c, times(1)).delete(any())
      }
    }

  }
}

object CacheIOEffectSpec {
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
    val cacheKey: CacheKey = CacheKey(id.value)
  }
  object TestModel1 extends CacheableModel[TestModel1] {
    import io.circe.generic.semiauto._
    override implicit val encoder: Encoder[TestModel1] = deriveEncoder
    override implicit val decoder: Decoder[TestModel1] = deriveDecoder
    val dbNum: DBNum                                   = DBNum(1)
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
    val cacheKey: CacheKey = CacheKey(id.value)
  }
  object TestModel2 extends CacheableModel[TestModel2] {
    import io.circe.generic.semiauto._
    override implicit val encoder: Encoder[TestModel2] = deriveEncoder
    override implicit val decoder: Decoder[TestModel2] = deriveDecoder
    val dbNum: DBNum                                   = DBNum(2)
  }

}
