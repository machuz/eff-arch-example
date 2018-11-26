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
import example.shared.lib.transactionTask.Transaction

class UserRepositoryImpl extends UserRepository with UserConverter {

  private val u = UserDataModel.syntax("u")

  override def resolveById[R: _task: _trantask: _readerDbSession](id: UserId): Eff[R, Option[User]] = {
    for {
      tran <- ask[R, Transaction]
      q <- {
        implicit val session: DBSession = fetchDBSession(tran)
        val query = withSQL {
          select
            .from(UserDataModel as u)
            .where
            .eq(u.id, id.value)
        }.map(UserDataModel(u.resultName))
          .single
          .apply
          .map(convertToDomainModel)
        read(query)
      }
    } yield q
//    val query: TransactionTask[ReadTransaction, Option[User]] =
//      sessionAsk.map { implicit session =>
//        withSQL {
//          select
//            .from(UserDataModel as u)
//            .where
//            .eq(u.id, id.value)
//        }.map(UserDataModel(u.resultName))
//          .single
//          .apply
//          .map(convertToDomainModel)
//      }
//    fromTranTask(query)
  }

  override def store[R: _task: _trantask: _readerDbSession](entity: User): Eff[R, User] = {
    for {
      tran <- ask[R, Transaction]
      q <- {
        implicit val session: DBSession = fetchDBSession(tran)
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

  override def remove[R: _trantask: _readerDbSession](id: UserId): Eff[R, Unit] = {
    for {
      tran <- ask[R, Transaction]
      q <- {
        implicit val session: DBSession = fetchDBSession(tran)
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
