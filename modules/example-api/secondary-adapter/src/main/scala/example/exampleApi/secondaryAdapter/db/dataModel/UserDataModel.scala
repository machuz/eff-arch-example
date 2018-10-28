package example.exampleApi.secondaryAdapter.db.dataModel

import scalikejdbc._
import java.time.ZonedDateTime

case class UserDataModel(id: String, name: Option[String], createdAt: ZonedDateTime, updatedAt: ZonedDateTime)

object UserDataModel extends SQLSyntaxSupport[UserDataModel] { self =>

  override val schemaName: Option[String] = None
  override val tableName: String          = "users"

  def apply(u: ResultName[UserDataModel])(rs: WrappedResultSet): UserDataModel = {
    UserDataModel(rs.string(u.id), rs.stringOpt(u.name), rs.dateTime(u.createdAt), rs.dateTime(u.updatedAt))
  }
}
