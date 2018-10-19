package example.exampleApi.secondaryAdapter.repository

import example.exampleApi.domain.model.user.{ User, UserId }
import example.exampleApi.domain.repository.user.UserRepository
import scalikejdbc._

class UserRepositoryImpl extends UserRepository {
  override def resolveById(id: UserId): Option[User] = ???
  override def store(entity: User): User             = ???
  override def remove(id: UserId): Unit              = ???

  sql"""
create table members (
  id serial not null primary key,
  name varchar(64),
  created_at timestamp not null
)
""".execute.apply()

}
