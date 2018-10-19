//package example.shared.lib.eff.cache
//
//import akka.util.ByteString
//import io.circe.{ Decoder, Encoder }
//import monix.eval.Task
//import redis.{ ByteStringFormatter, RedisReplyDeserializer }
//import redis.protocol.{ Bulk, RedisReply }
//
//import scalaz.{ -\/, \/, \/- }
//
//import example.shared.lib.dddSupport.Error
//import example.shared.lib.eff.cache.CacheIOTypes.{ CacheHashKey, CacheHashKeyGlob, CacheKey, CacheKeyGlob }
//
//abstract class RedisClient {
//
//  def put[A: ByteStringFormatter](
//    key: CacheKey,
//    value: A,
//    expireSeconds: Option[Long] = None
//  ): Task[\/[Error, A]]
//
//  def putList[A: ByteStringFormatter](
//    key: CacheKey,
//    values: Seq[A],
//    expireSeconds: Option[Long] = None
//  ): Task[\/[Error, Seq[A]]]
//
//  def putHash[A: ByteStringFormatter](
//    key: CacheKey,
//    hashKey: CacheHashKey,
//    value: A,
//    expireSeconds: Option[Long] = None
//  ): Task[\/[Error, A]]
//
//  def putBulkHash[A: ByteStringFormatter](
//    key: CacheKey,
//    values: Map[CacheHashKey, A],
//    expireSeconds: Option[Long] = None
//  ): Task[\/[Error, Seq[A]]]
//
//  def get[A: ByteStringFormatter](
//    key: CacheKey
//  ): Task[\/[Error, Option[A]]]
//
//  def getList[A: ByteStringFormatter](
//    key: CacheKey,
//    start: Int = 0,
//    stop: Int = -1
//  ): Task[\/[Error, Seq[A]]]
//
//  def getAllHash[A: ByteStringFormatter](
//    key: CacheKey
//  ): Task[\/[Error, Seq[A]]]
//
//  def scan[A: ByteStringFormatter](
//    matchGlob: CacheKeyGlob
//  ): Task[\/[Error, Seq[A]]]
//
//  def scanHash[A: ByteStringFormatter](
//    key: CacheKey,
//    hashKeyGlob: CacheHashKeyGlob
//  ): Task[\/[Error, Seq[A]]]
//
//  def delete(key: CacheKey): Task[\/[Error, Unit]]
//
//  def deleteHash(
//    key: CacheKey,
//    hashKeys: Seq[CacheHashKey]
//  ): Task[\/[Error, Unit]]
//
//  def exists(key: CacheKey): Task[\/[Error, Boolean]]
//  def clear: Task[\/[Error, Unit]]
//}
//
//object RedisClient {
//  import io.circe.syntax._
//  def byteStringFormatter[A](implicit encoder: Encoder[A], decoder: Decoder[A]): ByteStringFormatter[A] =
//    new ByteStringFormatter[A] {
//      def serialize(data: A): ByteString = ByteString(data.asJson.noSpaces)
//
//      def deserialize(bs: ByteString): A = {
//        \/.fromEither(io.circe.parser.decode[A](bs.utf8String)) match {
//          case \/-(r) => r
//          case -\/(e) => throw e
//        }
//      }
//
//      implicit val redisReplyDeserializer = new RedisReplyDeserializer[A] {
//        override def deserialize: PartialFunction[RedisReply, A] = {
//          case Bulk(Some(bs)) => byteStringFormatter.deserialize(bs)
//        }
//      }
//    }
//}
