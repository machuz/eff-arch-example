package example.exampleApi.secondaryAdapter.db.dataModel

import scalikejdbc._
import java.time.ZonedDateTime

case class User(id: String, name: Option[String], createdAt: ZonedDateTime, updatedAt: ZonedDateTime)

object User extends SQLSyntaxSupport[User] {

  override val schemaName: Option[String] = None
  override val tableName: String = "users"

  def apply(u: ResultName[User])(rs: WrappedResultSet): User = {
    User(rs.string(u.id), rs.stringOpt(u.name), rs.dateTime(u.createdAt), rs.dateTime(u.updatedAt))
  }
}