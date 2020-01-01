package example.shared.adapter.config.di

import com.google.inject.AbstractModule

import example.shared.adapter.secondary.eff.rdb.scalikejdbc.ScalikejdbcInterpreter
import example.shared.lib.dddSupport.domain.{ UUIDIdGenerator, UUIDIdGeneratorImpl }
import example.shared.lib.eff.db.transactionTask.TransactionTaskInterpreter
import example.shared.lib.eff.util.clock.java8.interpreter.{ ClockMInterpreter, ClockMInterpreterImpl }
import example.shared.lib.eff.util.idGen.interpreter.{ IdGenInterpreter, IdGenInterpreterImpl }

class InterpreterModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[UUIDIdGenerator]).to(classOf[UUIDIdGeneratorImpl])
    bind(classOf[IdGenInterpreter]).to(classOf[IdGenInterpreterImpl])
    bind(classOf[ClockMInterpreter]).to(classOf[ClockMInterpreterImpl])
    bind(classOf[TransactionTaskInterpreter]).to(classOf[ScalikejdbcInterpreter])
  }

}
