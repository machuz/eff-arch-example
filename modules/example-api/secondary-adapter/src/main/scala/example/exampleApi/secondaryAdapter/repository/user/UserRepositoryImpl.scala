package example.exampleApi.secondaryAdapter.repository.user

import org.atnos.eff.Eff

import scalikejdbc._
import example.exampleApi.domain.model.user.{ User, UserId }
import example.exampleApi.domain.repository.user.UserRepository
import example.exampleApi.secondaryAdapter.db.dataModel.UserDataModel
import example.shared.adapter.secondary.transactionTask.scalikejdbc._
import example.shared.adapter.secondary.rdb.scalikejdbc.pimp.RichMySQLSyntaxSupport._
import example.shared.adapter.secondary.eff._
import example.shared.lib.eff._
import example.shared.lib.eff.atnosEff._
import example.shared.lib.transactionTask.{ DbSession, Transaction }

class UserRepositoryImpl extends UserRepository with UserConverter {

  private val u = UserDataModel.syntax("u")

  override def resolveById[R: _task: _trantask: _readerDbSession: _stateTransaction](
    id: UserId
  ): Eff[R, Option[User]] = {
    for {
      abstractSession <- ask[R, DbSession]
      _               <- Transaction.read
      q <- {
        implicit val session: DBSession = fetchDbSession(abstractSession)
        val query = withSQL {
          select
            .from(UserDataModel as u)
            .where
            .eq(u.id, id.value)
        }.map { rs =>
          convertToDomainModel(UserDataModel(u.resultName)(rs))
        }.single
        read(query)
      }
    } yield q


//    Eff[R, SqlToOptoin[User, HasExtractor]]
  }

  override def store[R: _task: _trantask: _readerDbSession: _stateTransaction](entity: User): Eff[R, User] = {
    for {
      abstractSession <- ask[R, DbSession]
      _               <- Transaction.readWrite
      q <- {
        implicit val session: DBSession = fetchDbSession(abstractSession)
        val query = withSQL {
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
        write(query).map(_ => entity)
      }
    } yield q
//
//    val query: TransactionTask[ReadWriteTransaction, Int] =
//      sessionAsk.map { implicit session =>
////          Task.delay {
////            implicit val session: DBSession = fetchDBSession(tran)
//        withSQL {
//          insert
//            .into(UserDataModel)
//            .namedValues(
//              u.id        -> entity.id.value,
//              u.name      -> entity.name,
//              u.createdAt -> entity.createdAt,
//              u.updatedAt -> entity.updatedAt
//            )
//            .onDuplicateKeyUpdate(
//              u.id -> entity.id.value
//            )
//        }.update().apply()
//      }
//    fromTranTask(query).map(_ => entity)

//
//    val res: TransactionTask[ReadWriteTransaction, User] =
//      sessionAsk.map { implicit session =>
//        }
//    fromTranTask(res)
  }

  override def remove[R: _trantask: _readerDbSession: _stateTransaction](id: UserId): Eff[R, Unit] = {
    for {
      abstractSession <- ask[R, DbSession]
      _               <- Transaction.readWrite
      q <- {
        implicit val session: DBSession = fetchDbSession(abstractSession)
        val query = withSQL {
          delete
            .from(UserDataModel)
            .where
            .eq(u.id, id.value)
        }.update.apply()
        write(query).map(_ => ())
      }
    } yield q
  }

}
