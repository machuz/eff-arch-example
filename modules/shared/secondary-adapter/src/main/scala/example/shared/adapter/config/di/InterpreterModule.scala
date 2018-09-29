package example.shared.adapter.config.di

import com.google.inject.AbstractModule

import example.shared.lib.dddSupport.domain.{ UUIDIdGenerator, UUIDIdGeneratorImpl }
import example.shared.lib.eff.db.slick.DBComponent
import example.shared.lib.eff.util.clock.joda.{ JodaTimeUtils, JodaTimeUtilsImpl }
import example.shared.lib.eff.util.clock.joda.interpreter.{ JodaTimeMInterpreter, JodaTimeMInterpreterImpl }
import example.shared.lib.eff.util.idGen.interpreter.{ IdGenInterpreter, IdGenInterpreterImpl }

class InterpreterModule extends AbstractModule {

  def configure(): Unit = {
//    bind(classOf[DBComponent]).toProvider(classOf[DBProvider])
//    bind(classOf[DBIOInterpreter]).to(classOf[DBIOInterpreterImpl])
    bind(classOf[JodaTimeUtils]).to(classOf[JodaTimeUtilsImpl])
    bind(classOf[UUIDIdGenerator]).to(classOf[UUIDIdGeneratorImpl])
    bind(classOf[IdGenInterpreter]).to(classOf[IdGenInterpreterImpl])
    bind(classOf[JodaTimeMInterpreter]).to(classOf[JodaTimeMInterpreterImpl])
  }

}
