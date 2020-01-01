package example.shared.lib

import org.atnos.eff._
import org.atnos.eff.addon.monix.{ TaskCreation, TaskEffect }
import org.atnos.eff.syntax._
import org.atnos.eff.syntax.addon.monix.task

import cats.data.Writer
import example.shared.lib.dddSupport.Error
import example.shared.lib.eff.db.transactionTask.{ TransactionTaskCreation, TransactionTaskTypes }
import example.shared.lib.eff.util.clock.java8.{ ClockM, ClockMEffect }
import example.shared.lib.eff.util.idGen.{ IdGen, IdGenEffect }
import example.shared.lib.log.LogMessage

package object eff {

  type _errorEitherMember[R] = ErrorEither <= R
  type _errorEither[R]       = ErrorEither |= R
  type _nothing[R]           = Nothing |= R

  type IdGenStack  = Fx.fx1[IdGen]
  type ClockMStack = Fx.fx1[ClockM]

  type WriterLogMsg[A]  = Writer[LogMessage, A]
  type _writerLogMsg[R] = WriterLogMsg |= R

  type ErrorEither[A]  = Error Either A
  type ModelApplyStack = Fx.fx2[IdGen, ClockM]

  object myEff
    extends ClockMEffect
    with IdGenEffect
    with TransactionTaskTypes
    with TransactionTaskCreation
    with example.shared.lib.eff.either.ErrorEffect

  object atnosEff
    extends effOperations
    with effCats
    with ReaderEffect
    with WriterEffect
    with StateEffect
    with EvalEffect
    with OptionEffect
//    with ListEffect
//    with EitherEffect
    with ValidateEffect
    with ChooseEffect
    with SafeEffect
    with MemoEffect
    with Batch
    with EffInterpretation
    with EffCreation
    with EffImplicits
    with TaskEffect

  object atnosEffSyntax
    extends eff
    with reader
    with writer
    with state
    with eval
    with option
//    with list
//    with either
    with validate
    with choose
    with safe
    with memo
    with batch
    with task

  object atnosEffCreation
    extends ReaderCreation
    with WriterCreation
//    with StateCreation
    with EvalCreation
    with OptionCreation
    with ListCreation
    with EitherCreation
    with ValidateCreation
    with ChooseCreation
    with TaskCreation
    with MemoCreation
    with EffCreation
    with SafeCreation

}
