package example.exampleApi.secondaryAdapter.db.dataModel

import scalikejdbc._
import java.time.ZonedDateTime

case class UserDataModel(id: String, name: Option[String], createdAt: ZonedDateTime, updatedAt: ZonedDateTime)

object UserDataModel extends SQLSyntaxSupport[UserDataModel] { self =>

  override val schemaName: Option[String] = None
  override val tableName: String          = "users"

  def apply(u: ResultName[UserDataModel])(rs: WrappedResultSet): UserDataModel = {
    UserDataModel(
      id = rs.string(u.id),
      name = rs.stringOpt(u.name),
      createdAt = rs.dateTime(u.createdAt),
      updatedAt = rs.dateTime(u.updatedAt)
    )
  }
}
