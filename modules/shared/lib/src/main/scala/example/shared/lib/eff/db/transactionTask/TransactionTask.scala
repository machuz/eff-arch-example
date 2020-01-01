package example.shared.lib.eff.db.transactionTask

import monix.eval.Task

trait TransactionTask[+A] { lhs =>

  def execute(resource: DbSession): Task[A]

  def flatMap[B](f: A => TransactionTask[B]): TransactionTask[B] =
    new TransactionTask[B] {
      def execute(resource: DbSession): Task[B] =
        lhs.execute(resource).map(f).flatMap(_.execute(resource))
    }

  /**
    * 関数をTaskの結果に適用する
    *
    * @param f 適用したい関数
    * @tparam B 関数を適用して得られた値の型
    * @return 関数が適用されたTask
    */
  def map[B](f: A => B): TransactionTask[B] = flatMap(a => TransactionTask(f(a)))

  def zip[B](that: TransactionTask[B]): TransactionTask[(A, B)] = {
    that.flatMap(x => this.map((_, x)))
  }

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
      def execute(resource: DbSession): Task[A] = Task.delay(a)
    }

  def fromTask[A](a: Task[A]): TransactionTask[A] =
    new TransactionTask[A] {
      def execute(resource: DbSession): Task[A] = a
    }

  def raiseError[A](e: Throwable): TransactionTask[A] =
    new TransactionTask[A] {
      def execute(resource: DbSession): Task[A] = Task.raiseError(e)
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
