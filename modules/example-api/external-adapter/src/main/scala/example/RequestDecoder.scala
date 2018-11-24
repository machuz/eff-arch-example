package example
import example.circe.CirceDecoder
import example.user.dto.show.{ShowUserRequest, ShowUserRequestDecoder}
import io.circe.Decoder

sealed trait RequestDecoder[A <: Request] extends CirceDecoder[A]

object RequestDecoder {
  implicit object ShowUserRequestDecoder extends RequestDecoder[ShowUserRequest] {
    protected override implicit def decoder: Decoder[ShowUserRequest] = new ShowUserRequestDecoder().create
  }

}
