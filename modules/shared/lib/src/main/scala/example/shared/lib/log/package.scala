package example.shared.lib
import scalaz.{ @@, Tag }

package object log {
  trait _LogMessage
  type LogMessage = String @@ _LogMessage
  def LogMessage(v: String): LogMessage = Tag[String, _LogMessage](v)
}
