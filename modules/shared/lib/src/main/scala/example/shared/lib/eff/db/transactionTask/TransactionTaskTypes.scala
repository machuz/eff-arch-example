package example.shared.lib.eff.db.transactionTask

import org.atnos.eff.{ <=, |=, Fx }

import cats.data.Reader
import example.shared.lib.eff.ErrorEither

import scala.reflect.ClassTag
//import example.shared.lib.eff.db.transactionTask.TransactionTaskType.Resource
import example.shared.lib.transactionTask.{
  ReadTransaction,
  ReadWriteTransaction,
  Transaction,
  TransactionTask,
  TransactionTask2
}
import monix.eval.Task
//
//class Creature
//
//class Animal extends Creature
//
//class Cat extends Animal
//
//// +Tが共変、-Uが反変の意味
//// 共変 戻り値 最初に指定された型よりも強い派生型を使用できるようにします。
//// 反変 引数 最初に指定された型よりも一般的な (弱い派生の) 型を使用できるようにします。
//class Container[+T, -U] {
//
//  //引数でエラー
//  def f(arg: T): T = {
//    new T()
//  }
//
//  //戻り値でエラー
//  def f2(arg: U): T = {
//    new T()
//  }
//
//  //戻り値でエラー
//  def f3(): U = {
//    new U()
//  }
//
//}
//
//object A {
//  def main(args: Array[String]): Unit = {
//    val c = new Container[Cat, Animal]()
//    c.f(new Animal()) //Cat型の定義にAnimal型を渡せてしまう
//  }
//
//  //共変の型にCatを指定した場合のContainer定義
//  class Container[Cat, Animal] {
//    //f2, f3関数は省略
//
//    def f(arg: Cat): Cat = { //引数の型がCat型
//      new Cat()
//    }
//  }
//}
//

object TransactionTaskType {
//  type Resource <: Transaction with ReadTransaction with ReadWriteTransaction
//  type Resource >: ReadWriteTransaction <: Transaction
//  type Resource = Transaction >: ReadWriteTransaction
}

trait TransactionTaskTypes {

  type TranTask[A]  = TransactionTask[_>: ReadWriteTransaction <: Transaction, A]
  type _trantask[R] = TranTask |= R
//  type _trantask[R] = TranTask |= R
  type _TranTask[R] = TranTask <= R

  type TranTask2[A]  = TransactionTask2[A]
  type _trantask2[R] = TranTask2 |= R
  type _TranTask2[R] = TranTask2 <= R

//
//  type RTranTask[A]  = TransactionTask[ReadTransaction, A]
//  type _rtrantask[R] = RTranTask |= R
//  type _RTranTask[R] = RTranTask <= R
//
//  type WTranTask[A]  = TransactionTask[ReadWriteTransaction, A]
//  type _wtrantask[R] = WTranTask |= R
//  type _WTranTask[R] = WTranTask <= R

  type ReaderDbSession[A]  = Reader[Transaction, A]
  type _readerDbSession[R] = ReaderDbSession |= R

  type DBStack = Fx.fx3[TranTask, Task, ErrorEither]
}
