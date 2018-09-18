package example.shared.lib.eff.cache

import org.atnos.eff.|=

import scalaz.{ @@, Tag }

object CacheIOTypes {

  type _cacheio[R] = CacheIO |= R
  sealed trait _DBNum
  type DBNum = Int @@ _DBNum
  def DBNum(v: Int): DBNum = Tag[Int, _DBNum](v)

  sealed trait _CacheKey
  type CacheKey = String @@ _CacheKey
  def CacheKey(v: String): CacheKey = Tag[String, _CacheKey](v)

  sealed trait _CacheKeyGlob
  type CacheKeyGlob = String @@ _CacheKeyGlob
  def CacheKeyGlob(v: String): CacheKeyGlob = Tag[String, _CacheKeyGlob](v)

  sealed trait _CacheHashKey
  type CacheHashKey = String @@ _CacheHashKey
  def CacheHashKey(v: String): CacheHashKey = Tag[String, _CacheHashKey](v)

  sealed trait _CacheHashKeyGlob
  type CacheHashKeyGlob = String @@ _CacheHashKeyGlob
  def CacheHashKeyGlob(v: String): CacheHashKeyGlob = Tag[String, _CacheHashKeyGlob](v)

  val splitter = "___"
}
