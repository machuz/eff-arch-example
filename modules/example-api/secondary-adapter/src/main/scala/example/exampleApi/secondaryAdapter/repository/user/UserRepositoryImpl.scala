package example.exampleApi.secondaryAdapter.repository.user

import org.atnos.eff.Eff

import scalikejdbc._
import example.exampleApi.domain.model.user.{ User, UserId }
import example.exampleApi.domain.repository.user.UserRepository
import example.exampleApi.secondaryAdapter.db.dataModel.UserDataModel
import example.shared.adapter.secondary.rdb.scalikejdbc.pimp.RichMySQLSyntaxSupport._
import example.shared.adapter.secondary.eff.rdb.scalikejdbc.ScalikejdbcDbSession
import example.shared.lib.eff.myEff._

class UserRepositoryImpl extends UserRepository with UserConverter {

  private val u  = UserDataModel.syntax("u")
  private val uc = UserDataModel.column

  override def resolveById[R: _trantask](
    id: UserId
  ): Eff[R, Option[User]] = {
    for {
      q <- {
        val query = ScalikejdbcDbSession.sessionAsk.map { implicit session =>
          withSQL {
            select
              .from(UserDataModel as u)
              .where
              .eq(u.id, id.value)
          }.map { rs =>
              convertToDomainModel(UserDataModel(u.resultName)(rs))
            }
            .single
            .apply()
        }
        fromTranTask(query)
      }
    } yield q
  }

  override def store[R: _trantask](entity: User): Eff[R, User] = {
    for {
      q <- {
        val query = ScalikejdbcDbSession.sessionAsk
          .map { implicit session =>
            withSQL {
              insert
                .into(UserDataModel)
                .namedValues(
                  uc.id        -> entity.id.value,
                  uc.name      -> entity.name,
                  uc.createdAt -> entity.createdAt,
                  uc.updatedAt -> entity.updatedAt
                )
                .onDuplicateKeyUpdate(
                  uc.id -> entity.id.value
                )
            }.update().apply()
          }
          .map(_ => entity)
        fromTranTask(query)
      }
    } yield q

  }

  override def remove[R: _trantask](id: UserId): Eff[R, Unit] = {

    for {
      q <- {
        val query = ScalikejdbcDbSession.sessionAsk
          .map { implicit session =>
            withSQL {
              delete
                .from(UserDataModel as u)
                .where
                .eq(u.id, id.value)
            }.update.apply()
          }
          .map(_ => ())
        fromTranTask(query)
      }
    } yield q
  }

}
