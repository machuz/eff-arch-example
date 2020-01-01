package example.shared.lib.eff.db.transactionTask

import cats._
import cats.implicits._
import monix.eval.Task

object TransactionTaskInstances extends TransactionTaskInstances

trait TransactionTaskInstances {
  implicit def transactionTaskInstance: MonadError[TransactionTask, Throwable] with CoflatMap[TransactionTask] =
    new TransactionTaskCoflatMap with MonadError[TransactionTask, Throwable] {
      override def pure[A](x: A): TransactionTask[A] = TransactionTask(x)

      override def flatMap[A, B](fa: TransactionTask[A])(f: A => TransactionTask[B]): TransactionTask[B] =
        fa.flatMap(x => f(x))

      override def raiseError[A](e: Throwable): TransactionTask[A] = TransactionTask.raiseError(e)

      override def tailRecM[A, B](a: A)(f: A => TransactionTask[Either[A, B]]): TransactionTask[B] = {
        f(a).flatMap {
          case Left(a1) => tailRecM(a1)(f)
          case Right(b) => TransactionTask(b)
        }
      }

      override def handleErrorWith[A](
        fa: TransactionTask[A]
      )(f: Throwable => TransactionTask[A]): TransactionTask[A] = {
        fa.flatMap { x =>
          try {
            TransactionTask(x)
          } catch {
            case e: Throwable => f(e)
          }
        }
      }
    }

  private[db] abstract class TransactionTaskCoflatMap extends CoflatMap[TransactionTask] {
    def map[A, B](fa: TransactionTask[A])(f: A => B): TransactionTask[B] = fa.map(f)

    def coflatMap[A, B](fa: TransactionTask[A])(f: TransactionTask[A] => B): TransactionTask[B] =
      TransactionTask.fromTask(Task.delay(f(fa)))
  }

  private[db] class TransactionTaskSemigroup[A: Semigroup] extends Semigroup[TransactionTask[A]] {
    override def combine(fx: TransactionTask[A], fy: TransactionTask[A]): TransactionTask[A] =
      (fx zip fy).map { case (x, y) => x |+| y }
  }

  private[db] class TransactionTaskMonoid[A](implicit A: Monoid[A])
    extends TransactionTaskSemigroup[A]
    with Monoid[TransactionTask[A]] {
    def empty: TransactionTask[A] = TransactionTask(A.empty)
  }

  private[db] class TransactionTaskGroup[A](implicit A: Group[A])
    extends TransactionTaskMonoid[A]
    with Group[TransactionTask[A]] {
    def inverse(fx: TransactionTask[A]): TransactionTask[A] = fx.map(_.inverse())

    override def remove(fx: TransactionTask[A], fy: TransactionTask[A]): TransactionTask[A] =
      (fx zip fy).map { case (x, y) => x |-| y }
  }

}
