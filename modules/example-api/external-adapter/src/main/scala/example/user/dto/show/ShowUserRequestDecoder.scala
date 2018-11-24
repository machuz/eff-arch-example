package example.user.dto.show
import cats.implicits._
import io.circe.Decoder
import io.tabmo.circe.extra.rules.{ IntRules, StringRules }
import io.tabmo.json.rules._

class ShowUserRequestDecoder() {

  //TODO : Rulesはライブラリをラップしてメッセージとかちゃんとするようにする
  def create: Decoder[ShowUserRequest] =
    (userIdDecoder, nameDecoder, ageDecoder).mapN(ShowUserRequest.apply)

  private[this] def userIdDecoder: Decoder[String] =
    Decoder.instance(_.downField("user_id").read(StringRules.isNotEmpty()))
  private[this] def nameDecoder: Decoder[Name] = Decoder.instance(_.get[Name]("name"))
  private[this] def ageDecoder: Decoder[Int]   = Decoder.instance(_.downField("age").read(IntRules.positive()))

  private[this] implicit def name: Decoder[Name] = (firstNameDecoder, lastNameDecoder).mapN(Name.apply)

  private[this] def firstNameDecoder: Decoder[String] =
    Decoder.instance(_.downField("first_name").read(StringRules.isNotEmpty()))
  private[this] def lastNameDecoder: Decoder[String] =
    Decoder.instance(_.downField("last_name").read(StringRules.notBlank()))
}
