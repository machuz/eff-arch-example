package example.shared.adapter.config.support

import net.ceedubs.ficus.Ficus._
import com.typesafe.config.{ Config, ConfigFactory }

import scala.collection.JavaConverters.{ asScalaBufferConverter, iterableAsScalaIterableConverter }
import scala.collection.mutable

abstract class ApplicationConfBase {
  def conf: Config = ConfigFactory.load

  def throwException(key: String) = throw new NoSuchConfigException(s"$key is not defined in config files")

  def getString(key: String): String            = conf.getAs[String](key).getOrElse(throwException(key))
  def getStringOpt(key: String): Option[String] = conf.getAs[String](key)
  def getStringList(key: String): Seq[String]   = conf.getStringList(key).asScala

  def getInt(key: String): Int            = conf.getAs[Int](key).getOrElse(throwException(key))
  def getIntOpt(key: String): Option[Int] = conf.getAs[Int](key)
  def getIntList(key: String): Seq[Int]   = conf.getIntList(key).asScala.map(_.toInt)

  def getLong(key: String): Long            = conf.getAs[Long](key).getOrElse(throwException(key))
  def getLongOpt(key: String): Option[Long] = conf.getAs[Long](key)
  def getLongList(key: String): Seq[Long]   = conf.getLongList(key).asScala.map(_.toLong)

  def getBoolean(key: String): Boolean            = conf.getAs[Boolean](key).getOrElse(throwException(key))
  def getBooleanOpt(key: String): Option[Boolean] = conf.getAs[Boolean](key)

}
