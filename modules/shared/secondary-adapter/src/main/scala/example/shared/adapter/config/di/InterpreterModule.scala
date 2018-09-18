package example.shared.adapter.config.di

import com.google.inject.AbstractModule

import jp.eigosapuri.es.shared.adapter.secondary.eff.cache.interpreter.{ CacheIOInterpreter, CacheIOInterpreterImpl }
import jp.eigosapuri.es.shared.adapter.secondary.eff.db.interpreter.{ DBIOInterpreter, DBIOInterpreterImpl }
import jp.eigosapuri.es.shared.adapter.secondary.eff.db.slick.DBProvider
import jp.eigosapuri.es.shared.adapter.secondary.eff.push.interpreter.{ PushIOInterpreter, PushIOInterpreterImpl }
import jp.eigosapuri.es.shared.lib.dddSupport.domain.{ UUIDIdGenerator, UUIDIdGeneratorImpl }
import jp.eigosapuri.es.shared.lib.eff.db.slick.DBComponent
import jp.eigosapuri.es.shared.lib.eff.util.clock.joda.{ JodaTimeUtils, JodaTimeUtilsImpl }
import jp.eigosapuri.es.shared.lib.eff.util.clock.joda.interpreter.{ JodaTimeMInterpreter, JodaTimeMInterpreterImpl }
import jp.eigosapuri.es.shared.lib.eff.util.idGen.interpreter.{ IdGenInterpreter, IdGenInterpreterImpl }

class InterpreterModule extends AbstractModule {

  def configure(): Unit = {
    bind(classOf[DBComponent]).toProvider(classOf[DBProvider])
    bind(classOf[DBIOInterpreter]).to(classOf[DBIOInterpreterImpl])
    bind(classOf[JodaTimeUtils]).to(classOf[JodaTimeUtilsImpl])
    bind(classOf[UUIDIdGenerator]).to(classOf[UUIDIdGeneratorImpl])
    bind(classOf[CacheIOInterpreter]).to(classOf[CacheIOInterpreterImpl])
    bind(classOf[PushIOInterpreter]).to(classOf[PushIOInterpreterImpl])
    bind(classOf[IdGenInterpreter]).to(classOf[IdGenInterpreterImpl])
    bind(classOf[JodaTimeMInterpreter]).to(classOf[JodaTimeMInterpreterImpl])
  }

}
