package example.shared.lib.transactionTask

import org.atnos.eff.Member

import cats.Monad
import example.shared.lib.eff._
import monix.eval.Task

import scala.concurrent.{ ExecutionContext, Future }

/**
  * 『PofEAA』の「Unit of Work」パターンの実装
  *
  * トランザクションとはストレージに対するまとまった処理である
  * トランザクションオブジェクトとはトランザクションを表現するオブジェクトで、
  * 具体的にはデータベースライブラリのセッションオブジェクトなどが該当する
  *
  * @tparam Resource トランザクションオブジェクトの型
  * @tparam A トランザクションを実行して得られる値の型
  */
//trait ReadTransactionTask[-Resource, +A]      extends TransactionTask[Resource, A]
//trait ReadWriteTransactionTask[-Resource, +A] extends ReadTransactionTask[Resource, A]

trait TransactionTask[+A] {
  def execute(resource: DbSession): Task[A]
}

object TransactionTask {

  /**
    * TransactionTaskのデータコンストラクタ
    *
    * @param a TransactionTaskの値
    * @tparam A TransactionTaskの値の型
    * @return 実行するとaの値を返すTransactionTask
    */
  def apply[A](a: => A): TransactionTask[A] =
    new TransactionTask[A] {
      def execute(resource: DbSession): Task[A] = Task.now(a)
    }
}

/**
  * TransactionTaskを実行する
  * トランザクションオブジェクトの型ごとにインスタンスを作成すること
  *
  * @tparam Resource トランザクションオブジェクトの型
  */
trait TransactionTaskRunner[Resource] {

  /**
    * TransactionTaskを実行する
    *
    * @param task 実行するTransactionTask
    * @tparam A TransactionTask実行すると得られる値の型
    * @return TransactionTask実行して得られた値
    */
  def run[A](task: TransactionTask[A]): Task[A]
}
