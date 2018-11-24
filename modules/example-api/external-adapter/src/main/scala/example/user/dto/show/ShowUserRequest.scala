package example.user.dto.show
import example.akkaHttp.Request


case class ShowUserRequest(userId: String, name: Name, age: Int) extends Request
case class Name(firstName: String, lastName: String)


