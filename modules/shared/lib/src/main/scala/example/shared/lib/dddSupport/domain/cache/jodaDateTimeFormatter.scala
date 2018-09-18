package example.shared.lib.dddSupport.domain.cache

import org.joda.time.DateTime
import org.joda.time.format.{ DateTimeFormat, DateTimeFormatter }

import io.circe.{ Decoder, DecodingFailure, Encoder, Json }

import example.shared.lib.pimp.RichDateTime._

object jodaDateTimeFormatter {
  final def decodeDateTime(formatter: DateTimeFormatter): Decoder[DateTime] =
    Decoder.instance { c =>
      c.as[String] match {
        case Right(s) =>
          try Right(DateTime.parse(s, formatter))
          catch {
            case _: IllegalArgumentException => Left(DecodingFailure("DateTime", c.history))
          }
        case l @ Left(_) => l.asInstanceOf[Decoder.Result[DateTime]]
      }
    }

  final def encodeDateTime(formatter: DateTimeFormatter): Encoder[DateTime] =
    Encoder.instance(time => Json.fromString(time.toUTC.toString))

  val fmt: DateTimeFormatter                                       = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
  implicit final val decodeZonedDateTimeDefault: Decoder[DateTime] = decodeDateTime(fmt)
  implicit final val encodeZonedDateTimeDefault: Encoder[DateTime] = encodeDateTime(fmt)
}
