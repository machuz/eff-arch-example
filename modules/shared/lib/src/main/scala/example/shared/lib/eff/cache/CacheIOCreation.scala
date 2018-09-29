package example.shared.lib.eff.cache

import org.atnos.eff.Eff

import redis.{ ByteStringFormatter, Cursor }

import example.shared.lib.eff.cache.CacheIO._
import example.shared.lib.eff.cache.CacheIOTypes._

trait CacheIOCreation {

  def store[T, R: _cacheio](
    key: CacheKey,
    value: T,
    expireSecond: Option[Long] = None
  )(implicit fmt: ByteStringFormatter[T]): Eff[R, T] =
    Eff.send[CacheIO, R, T](Put(key, value, expireSecond, fmt))

  def storeList[T, R: _cacheio](
    key: CacheKey,
    values: Seq[T],
    expireSecond: Option[Long] = None
  )(implicit fmt: ByteStringFormatter[T]): Eff[R, Seq[T]] =
    Eff.send[CacheIO, R, Seq[T]](PutList(key, values, expireSecond, fmt))

  def storeHash[T, R: _cacheio](
    key: CacheKey,
    hashKey: CacheHashKey,
    value: T,
    expireSecond: Option[Long] = None
  )(implicit fmt: ByteStringFormatter[T]): Eff[R, T] =
    Eff.send[CacheIO, R, T](PutHash(key, hashKey, value, expireSecond, fmt))

  def storeBulkHash[T, R: _cacheio](
    key: CacheKey,
    values: Map[CacheHashKey, T],
    expireSecond: Option[Long] = None
  )(implicit fmt: ByteStringFormatter[T]): Eff[R, Seq[T]] =
    Eff.send[CacheIO, R, Seq[T]](PutBulkHash(key, values, expireSecond, fmt))

  def find[T, R: _cacheio](key: CacheKey)(implicit fmt: ByteStringFormatter[T]): Eff[R, Option[T]] =
    Eff.send[CacheIO, R, Option[T]](Get[T](key, fmt))

  def findList[T, R: _cacheio](key: CacheKey)(implicit fmt: ByteStringFormatter[T]): Eff[R, Seq[T]] =
    Eff.send[CacheIO, R, Seq[T]](GetList[T](key, fmt))

  def findAllHash[T, R: _cacheio](key: CacheKey)(implicit fmt: ByteStringFormatter[T]): Eff[R, Seq[T]] =
    Eff.send[CacheIO, R, Seq[T]](GetAllHash[T](key, fmt))

  def findByMatchGlob[T, R: _cacheio](matchGlob: CacheKeyGlob)(
    implicit fmt: ByteStringFormatter[T]
  ): Eff[R, Option[T]] =
    Eff.send[CacheIO, R, Option[T]](ScanOne[T](matchGlob, fmt))

  def findAllByMatchGlob[T, R: _cacheio](
    matchGlob: CacheKeyGlob
  )(implicit fmt: ByteStringFormatter[T]): Eff[R, Seq[T]] =
    Eff.send[CacheIO, R, Seq[T]](Scan[T](matchGlob, fmt))

  def findHashByMatchGlob[T, R: _cacheio](
    key: CacheKey,
    glob: CacheHashKeyGlob
  )(implicit fmt: ByteStringFormatter[T]): Eff[R, Option[T]] =
    Eff.send[CacheIO, R, Option[T]](ScanHashOne[T](key, glob, fmt))

  def findHashAllByMatchGlob[T, R: _cacheio](
    key: CacheKey,
    glob: CacheHashKeyGlob
  )(implicit fmt: ByteStringFormatter[T]): Eff[R, Seq[T]] =
    Eff.send[CacheIO, R, Seq[T]](ScanHash[T](key, glob, fmt))

  def delete[R: _cacheio](key: CacheKey): Eff[R, Unit] =
    Eff.send(Delete(key))

  def deleteHash[R: _cacheio](key: CacheKey, hashKeys: Seq[CacheHashKey]): Eff[R, Unit] =
    Eff.send(DeleteHash(key, hashKeys))

  def exists[R: _cacheio](key: CacheKey): Eff[R, Boolean] =
    Eff.send(Has(key))

  def clear[R: _cacheio]: Eff[R, Unit] =
    Eff.send(Clear)
}
