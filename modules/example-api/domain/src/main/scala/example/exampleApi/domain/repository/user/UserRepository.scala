package example.exampleApi.domain.repository.user

import org.atnos.eff.Eff

import example.exampleApi.domain.model.user.{ User, UserId }
import example.shared.lib.dddSupport.adapter.secondary.repository.Repository
import example.shared.lib.eff._
import example.shared.lib.eff.atnosEff._task

abstract class UserRepository extends Repository[UserId, User] {
  def resolveById[R: _task: _trantask: _readerDbSession](id: UserId): Eff[R, Option[User]]
  def store[R: _task: _trantask: _readerDbSession](entity: User): Eff[R, User]
  def remove[R: _trantask: _readerDbSession](id: UserId): Eff[R, Unit]
}
