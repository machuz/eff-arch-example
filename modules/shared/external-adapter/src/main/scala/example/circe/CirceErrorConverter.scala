package example.circe
import example.shared.lib.dddSupport.Error.FieldError
import example.shared.lib.dddSupport.Error
import example.shared.lib.dddSupport.ErrorCode
import io.circe.CursorOp.DownField
import io.circe.DecodingFailure

trait CirceErrorConverter {
  def convert(circeError: io.circe.Error): Error = circeError match {
    case DecodingFailure(message, dfs: List[DownField]) =>
      FieldError(ErrorCode.INVALID_FORM_VALUE_ERROR, mkString(dfs), message)
    case _ => ???
  }

  private[this] def mkString(dfs: List[DownField]): String = dfs.map(_.k).reverse.mkString("/")
}
