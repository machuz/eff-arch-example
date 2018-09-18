package example.shared.lib.test.eff.cache.interpreter

import org.atnos.eff.interpret.interpretUnsafe
import org.atnos.eff.{ <=, Eff, SideEffect }

import akka.util.ByteString

import scala.collection.mutable

import scalaz.Tag

import jp.eigosapuri.es.shared.lib.eff.cache.CacheIO
import jp.eigosapuri.es.shared.lib.eff.cache.CacheIO._
import jp.eigosapuri.es.shared.lib.eff.cache.CacheIOTypes.{ CacheHashKey, CacheKey }

trait CacheIOTestInterpreter {

  def run[R, A](effects: Eff[R, A])(implicit m: CacheIO <= R): Eff[m.Out, A] = {

    import cats.implicits._

    val kvs     = mutable.Map.empty[CacheKey, String]
    val kvsHash = mutable.Map.empty[CacheKey, mutable.Map[CacheHashKey, String]]

    val sideEffect = new SideEffect[CacheIO] {
      def apply[X](kv: CacheIO[X]): X =
        kv match {
          case x: Put[X] =>
            println(s"put(${x.key}, ${x.value}, ${x.expireSeconds})")
            kvs updated (x.key, x.fmt.serialize(x.value).utf8String)
            ().asInstanceOf[X]

          case PutList(key, values, expireSeconds, fmt) =>
            println(s"putList($key, $values, $expireSeconds)")
            kvs updated (key, values.map(y => fmt.serialize(y).utf8String))
            ().asInstanceOf[X]

          case x: PutHash[X] =>
            println(s"putList(${x.key}, ${x.hashKey}, ${x.value}, ${x.expireSeconds})")
            kvsHash updated (x.key, x.hashKey -> x.fmt.serialize(x.value).utf8String)
            ().asInstanceOf[X]

          case PutBulkHash(key, values, expireSeconds, fmt) =>
            println(s"putList($key, $values, $expireSeconds)")
            kvsHash updated (key, values.map { y =>
              y._1 -> fmt.serialize(y._2).utf8String
            })
            ().asInstanceOf[X]

          case Get(key, fmt) =>
            println(s"get($key)")
            kvs.get(key).map(x => fmt.deserialize(ByteString(x))).asInstanceOf[X]

          case GetList(key, fmt) =>
            println(s"getList($key)")
            kvs.get(key).map(x => fmt.deserialize(ByteString(x))).asInstanceOf[X]

          case GetAllHash(key, fmt) =>
            println(s"getAllHash($key)")
            kvsHash
              .get(key)
              .map(
                x =>
                  x.values.map { v =>
                    fmt.deserialize(ByteString(v))
                }
              )
              .asInstanceOf[X]

          case Scan(keyBlob, fmt) =>
            println(s"scan($keyBlob)")
            val regex = Tag.unwrap(keyBlob).r
            kvs
              .filter(x => regex.findFirstIn(Tag.unwrap(x._1)).nonEmpty)
              .values
              .map(x => fmt.deserialize(ByteString(x)))
              .asInstanceOf[X]

          case ScanOne(keyBlob, fmt) =>
            println(s"scanOne($keyBlob)")
            val regex = Tag.unwrap(keyBlob).r
            kvs
              .filter(x => regex.findFirstIn(Tag.unwrap(x._1)).nonEmpty)
              .values
              .headOption
              .map(x => fmt.deserialize(ByteString(x)))
              .asInstanceOf[X]

          case ScanHash(key, hashKeyGlob, fmt) =>
            println(s"scanHash($key, $hashKeyGlob)")
            val regex = Tag.unwrap(hashKeyGlob).r
            kvsHash
              .filter(_._1 == key)
              .filter(x => regex.findFirstIn(Tag.unwrap(x._1)).nonEmpty)
              .values
              .map(x => x.map(y => fmt.deserialize(ByteString(y._2))))
              .asInstanceOf[X]

          case ScanHashOne(key, hashKeyGlob, fmt) =>
            println(s"scanHashOne($key, $hashKeyGlob)")
            val regex = Tag.unwrap(hashKeyGlob).r
            kvs
              .filter(_._1 == key)
              .filter(x => regex.findFirstIn(Tag.unwrap(x._1)).nonEmpty)
              .values
              .headOption
              .map(x => fmt.deserialize(ByteString(x)))
              .asInstanceOf[X]

          case Delete(key) =>
            println(s"delete($key)")
            kvs - key
            ().asInstanceOf[X]

          case DeleteHash(key, hashKeys) =>
            println(s"delete($key,$hashKeys)")
            kvsHash.map { x =>
              if (x._1 == key) hashKeys.map { y =>
                (x._1, x._2 - y)
              } else x
            }
            ().asInstanceOf[X]

          case Has(key) =>
            println(s"has($key)")
            kvs.isDefinedAt(key).asInstanceOf[X]

          case Clear =>
            println(s"clear")
            kvs.empty.asInstanceOf[X]
        }
      def applicative[X, Tr[_]: cats.Traverse](ms: Tr[CacheIO[X]]): Tr[X] =
        ms.map(apply)
    }
    interpretUnsafe(effects)(sideEffect)(m)
  }

}
