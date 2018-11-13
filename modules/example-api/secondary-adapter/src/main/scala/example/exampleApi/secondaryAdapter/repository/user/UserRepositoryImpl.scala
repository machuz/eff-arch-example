package example.exampleApi.secondaryAdapter.repository.user

import org.atnos.eff.Eff

import scalikejdbc._

import example.exampleApi.domain.model.user.{ User, UserId }
import example.exampleApi.domain.repository.user.UserRepository
import example.exampleApi.secondaryAdapter.db.dataModel.UserDataModel
import example.shared.adapter.secondary.transactionTask.scalikejdbc._
import example.shared.adapter.secondary.rdb.scalikejdbc.pimp.RichMySQLSyntaxSupport._
import example.shared.lib.eff._

class UserRepositoryImpl extends UserRepository with UserConverter {

  private val u = UserDataModel.syntax("u")

  override def resolveById[R: _trantask](id: UserId): Eff[R, Option[User]] = {
    val res = sessionAsk.map { implicit session =>
      withSQL {
        select
          .from(UserDataModel as u)
          .where
          .eq(u.id, id.value)
      }.map(UserDataModel(u.resultName))
        .single
        .apply
        .map(convertToDomainModel)
    }
    fromTranTask(res)
  }

  override def store[R: _trantask](entity: User): Eff[R, User] = {
    val res = sessionAsk.map { implicit session =>
      withSQL {
        insert
          .into(UserDataModel)
          .namedValues(
            u.id        -> entity.id.value,
            u.name      -> entity.name,
            u.createdAt -> entity.createdAt,
            u.updatedAt -> entity.updatedAt
          )
          .onDuplicateKeyUpdate(
            u.id -> entity.id.value
          )
      }.update().apply()
      entity
    }
    fromTranTask(res)
  }

  override def remove[R: _trantask](id: UserId): Eff[R, Unit] = {
    val res = sessionAsk
      .map { implicit session =>
        withSQL {
          delete
            .from(UserDataModel)
            .where
            .eq(u.id, id.value)
        }.update.apply()
      }
      .map(_ => ())
    fromTranTask(res)
  }

}
