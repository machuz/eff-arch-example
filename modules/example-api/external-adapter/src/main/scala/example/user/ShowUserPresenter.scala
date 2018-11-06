package example.user

import example.akkaHttp.AbstractAkkaHttpPresenter
import example.exampleApi.domain.model.user.User
import example.shared.lib.dddSupport.Error
import scalaz.\/

class ShowUserPresenter extends AbstractAkkaHttpPresenter[\/[Error, User]] {
  override def response(arg: Arg): Rendered = ???
}
