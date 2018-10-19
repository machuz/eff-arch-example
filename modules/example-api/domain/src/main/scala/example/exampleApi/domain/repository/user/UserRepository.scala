package example.exampleApi.domain.repository.user
import example.exampleApi.domain.model.user.{ User, UserId }
import example.shared.lib.dddSupport.adapter.secondary.repository.Repository

abstract class UserRepository extends Repository[UserId, User] {
  def resolveById(id: UserId): Option[User]
  def store(entity: User): User
  def remove(id: UserId): Unit
}
