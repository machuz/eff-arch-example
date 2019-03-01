package example.circe
import example.shared.lib.dddSupport.Error.{FieldError, FormValidationError}
import example.shared.lib.dddSupport.{Error, ErrorCode}
import io.circe.CursorOp.DownField
import io.circe.DecodingFailure

import scala.reflect.ClassTag

trait CirceErrorConverter {
  def convert[T <: DownField: ClassTag](circeError: io.circe.Error): Error = circeError match {
    case DecodingFailure(message, dfs: List[T]) =>
      FieldError(ErrorCode.INVALID_FORM_VALUE_ERROR, mkString(dfs), message)
    case _ => ???
  }

  def convert[T <: DecodingFailure: ClassTag](circeErrors: List[io.circe.Error]): Error = circeErrors match {
    case errs: List[T] =>
      errs.map(convert) match {
        case fieldErrors: List[FieldError] => FormValidationError(ErrorCode.INVALID_FORM_VALUE_ERROR, fieldErrors)
        case _                             => ???
      }

    case _ => ???
  }

  private[this] def mkString(dfs: List[DownField]): String = dfs.map(_.k).reverse.mkString("/")
}
