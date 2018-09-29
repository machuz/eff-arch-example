package example.shared.lib

import org.atnos.eff.{ <=, |=, Fx }

import cats.data.Writer
import example.shared.lib.dddSupport.Error
import example.shared.lib.eff.util.clock.joda.JodaTimeM
import example.shared.lib.eff.util.idGen.IdGen
import example.shared.lib.log.LogMessage

package object eff {
  type WriterLogMsg[A]  = Writer[LogMessage, A]
  type _writerLogMsg[R] = WriterLogMsg |= R

  type ErrorEither[A]        = Error Either A
  type _errorEitherMember[R] = ErrorEither <= R
  type _errorEither[R]       = ErrorEither |= R
  type _nothing[R]           = Nothing |= R

  type ModelApplyStack = Fx.fx2[IdGen, JodaTimeM]

}
