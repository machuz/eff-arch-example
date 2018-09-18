package example.shared.adapter.config.di

import com.google.inject.AbstractModule

import jp.eigosapuri.es.shared.adapter.secondary.eff.cache.interpreter.CacheIOInterpreter
import jp.eigosapuri.es.shared.adapter.secondary.eff.cache.rediscala.RedisClientProvider
import jp.eigosapuri.es.shared.lib.eff.cache.RedisClient

class RedisModule extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[RedisClient]).toProvider(classOf[RedisClientProvider])
  }
}
