package example.shared.lib

import org.atnos.eff.{ <=, |=, Fx }

import cats.data.Writer

import jp.eigosapuri.es.shared.domain.log.LogMessage
import jp.eigosapuri.es.shared.lib.dddSupport.EsError
import jp.eigosapuri.es.shared.lib.eff.util.clock.joda.JodaTimeM
import jp.eigosapuri.es.shared.lib.eff.util.idGen.IdGen

package object eff {
  type WriterLogMsg[A]  = Writer[LogMessage, A]
  type _writerLogMsg[R] = WriterLogMsg |= R

  type ErrorEither[A]        = EsError Either A
  type _errorEitherMember[R] = ErrorEither <= R
  type _errorEither[R]       = ErrorEither |= R
  type _nothing[R]           = Nothing |= R

  type ModelApplyStack = Fx.fx2[IdGen, JodaTimeM]

}
