//package example.shared.lib.eff.cache
//
//import example.shared.lib.eff.cache.CacheIOTypes._
//import redis.ByteStringFormatter
//
//sealed abstract class CacheIO[+A] {}
//
//object CacheIO extends CacheIOCreation {
//
//  case class Put[T](
//    key: CacheKey,
//    value: T,
//    expireSeconds: Option[Long],
//    fmt: ByteStringFormatter[T]
//  ) extends CacheIO[T]
//
//  case class PutList[T](
//    key: CacheKey,
//    values: Seq[T],
//    expireSeconds: Option[Long],
//    fmt: ByteStringFormatter[T]
//  ) extends CacheIO[Seq[T]]
//
//  case class PutHash[T](
//    key: CacheKey,
//    hashKey: CacheHashKey,
//    value: T,
//    expireSeconds: Option[Long] = None,
//    fmt: ByteStringFormatter[T]
//  ) extends CacheIO[T]
//
//  case class PutBulkHash[T](
//    key: CacheKey,
//    values: Map[CacheHashKey, T],
//    expireSeconds: Option[Long] = None,
//    fmt: ByteStringFormatter[T]
//  ) extends CacheIO[Seq[T]]
//
//  case class Get[T](
//    key: CacheKey,
//    fmt: ByteStringFormatter[T]
//  ) extends CacheIO[Option[T]]
//
//  case class GetList[T](
//    key: CacheKey,
//    fmt: ByteStringFormatter[T]
//  ) extends CacheIO[Seq[T]]
//
//  case class GetAllHash[T](
//    key: CacheKey,
//    fmt: ByteStringFormatter[T]
//  ) extends CacheIO[Seq[T]]
//
//  case class Scan[T](
//    matchGlob: CacheKeyGlob,
//    fmt: ByteStringFormatter[T]
//  ) extends CacheIO[Seq[T]]
//
//  case class ScanOne[T](
//    matchGlob: CacheKeyGlob,
//    fmt: ByteStringFormatter[T]
//  ) extends CacheIO[Option[T]]
//
//  case class ScanHash[T](
//    key: CacheKey,
//    hashKeyGlob: CacheHashKeyGlob,
//    fmt: ByteStringFormatter[T]
//  ) extends CacheIO[Seq[T]]
//
//  case class ScanHashOne[T](
//    key: CacheKey,
//    hashKeyGlob: CacheHashKeyGlob,
//    fmt: ByteStringFormatter[T]
//  ) extends CacheIO[Option[T]]
//
//  case class Delete(
//    key: CacheKey
//  ) extends CacheIO[Unit]
//
//  case class DeleteHash(
//    key: CacheKey,
//    hashKeys: Seq[CacheHashKey]
//  ) extends CacheIO[Unit]
//
//  case class Has(
//    key: CacheKey
//  ) extends CacheIO[Boolean]
//
//  case object Clear extends CacheIO[Unit]
//
//}
