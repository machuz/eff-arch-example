package example.shared.lib

import org.atnos.eff.addon.monix.TaskEffect
import org.atnos.eff.{ <=, |=, Fx }

import cats.data.Writer
import example.shared.lib.dddSupport.Error
import example.shared.lib.eff.db.transactionTask.TransactionTaskCreation
import example.shared.lib.eff.util.clock.java8.{ ClockM, ClockMEffect }
import example.shared.lib.eff.util.idGen.{ IdGen, IdGenEffect }
import example.shared.lib.log.LogMessage

/**
  * Note:
  * InterpreterがlibいあるものはEffectをmix-inするが、secondaryAdapternにあるものはCreationだけmix-inする
  */
package object eff extends TransactionTaskCreation with TaskEffect with ClockMEffect with IdGenEffect {

  type WriterLogMsg[A]  = Writer[LogMessage, A]
  type _writerLogMsg[R] = WriterLogMsg |= R

  type ErrorEither[A]        = Error Either A
  type _errorEitherMember[R] = ErrorEither <= R
  type _errorEither[R]       = ErrorEither |= R
  type _nothing[R]           = Nothing |= R

  type IdGenStack      = Fx.fx1[IdGen]
  type ClockMStack     = Fx.fx1[ClockM]
  type ModelApplyStack = Fx.fx2[IdGen, ClockM]

}
