package example.exampleApi.secondaryAdapter.repository.user

import org.atnos.eff.Eff

import cats.data.Reader
import scalikejdbc._
import example.exampleApi.domain.model.user.{ User, UserId }
import example.exampleApi.domain.repository.user.UserRepository
import example.exampleApi.secondaryAdapter.db.dataModel.UserDataModel
import example.shared.adapter.secondary.eff.rdb.scalikejdbc.{ ReadTransactionTask2, ReadWriteTransactionTask2 }
import example.shared.adapter.secondary.transactionTask.scalikejdbc._
import example.shared.adapter.secondary.rdb.scalikejdbc.pimp.RichMySQLSyntaxSupport._
import example.shared.lib.eff._
import example.shared.lib.eff.atnosEff._
import example.shared.lib.transactionTask.{ ReadTransaction, ReadWriteTransaction, Transaction, TransactionTask }
import monix.eval.Task

class UserRepositoryImpl extends UserRepository with UserConverter {

  private val u = UserDataModel.syntax("u")

  override def resolveById[R: _task: _trantask2: _readerDbSession](id: UserId): Eff[R, Option[User]] = {
    val x = for {
      tran <- ask[R, Transaction]
      q <- {
        val query =
          Task.delay {
            implicit val session: DBSession = fetchDBSession(tran)
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
        fromTask(query)
      }
      res <- {
        fromTranTask2(ReadTransactionTask2(q))
      }
    } yield res
    x
  }

  override def store[R: _task: _trantask2: _readerDbSession](entity: User): Eff[R, User] = {
    val x = for {
      tran <- ask[R, Transaction]
      q <- {
        val query =
          Task.delay {
            implicit val session: DBSession = fetchDBSession(tran)
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
          }
        fromTask(query)
      }
      res <- fromTranTask2(ReadWriteTransactionTask2(q)).map(_ => entity)
    } yield res
    x
//
//    val res: TransactionTask[ReadWriteTransaction, User] =
//      sessionAsk.map { implicit session =>
//        }
//    fromTranTask(res)
  }

  override def remove[R: _trantask](id: UserId): Eff[R, Unit] = ???
//  {
//    val res: TransactionTask[ReadWriteTransaction, Unit] =
//      sessionAsk
//        .map { implicit session =>
//          withSQL {
//            delete
//              .from(UserDataModel)
//              .where
//              .eq(u.id, id.value)
//          }.update.apply()
//        }
//        .map(_ => ())
//    fromTranTask(res)
//  }

}
