//package example.shared.adapter.secondary.eff.rdb.scalikejdbc
//
//import org.atnos.eff.{ Eff, Member, Translate }
//import org.atnos.eff.interpret.translate
//
//import example.shared.adapter.secondary.transactionTask.scalikejdbc._
//import example.shared.lib.eff._
//import example.shared.lib.eff.atnosEff._
//import example.shared.lib.transactionTask.{ ReadTransaction, ReadWriteTransaction }
////import example.shared.lib.eff.db.transactionTask.TransactionTaskType.Resource
//import example.shared.lib.eff.util.clock.java8.ClockMInterpretationTypes.ReaderClock
//import example.shared.lib.transactionTask.{ Transaction, TransactionTask, TransactionTask2 }
//import monix.eval.Task
//import scalikejdbc.DB
//
//trait ReadTransactionTask2[A] extends TransactionTask2[A] {
////  override val value: A
//}
//object ReadTransactionTask2 {
//  def apply[A](v: A): ReadTransactionTask2[A] = new ReadTransactionTask2[A] {
////    override val value: A = v
//  }
//}
//trait ReadWriteTransactionTask2[A] extends ReadTransactionTask2[A] {
////  override val value: A
//}
//object ReadWriteTransactionTask2 {
//  def apply[A](v: A): ReadWriteTransactionTask2[A] = new ReadWriteTransactionTask2[A] {
////    override val value: A = v
////    override def execute(
////      transaction: Transaction
////    ): Task[A] =
////      ???
//  }
//}
//
//trait ScalikejdbcInterpreter {
//
////  type TranTask[X] = TransactionTask[Transaction, X]
//
//  def runTransaction[R, U, A, B](
//    effect: Eff[R, A]
//  )(
//    implicit t1: Member.Aux[TranTask, R, U],
////    t2: Member.Aux[RTranTask, R, U],
////    t3: Member.Aux[WTranTask, R, U],
//    m1: _task[U],
//    m2: _errorEither[U]
////    m3: _readerDbSession[U]
//  ): Eff[U, A] = {
//
//    translate(effect)(new Translate[TranTask, U] {
//      def apply[X](ax: TranTask[X]): Eff[U, X] = {
//
//        import scala.reflect.runtime.universe._
////        def recognize[T](x: T)(implicit tag: TypeTag[T]): String =
////          tag.tpe match {
////            case TypeRef(utype, usymbol, args) =>
////              List(utype, usymbol, args).mkString("\n")
////          }
////        val a = ax.isInstanceOf[TransactionTask[ReadTransaction, X]]
////        val x = ax.isInstanceOf[TransactionTask[ReadWriteTransaction, X]]
//
////        println(recognize(ax))
////        println(a + s"read?")
////        println(x + s"write?")
//        println(ax.toString)
//        val i = ax.run()
//        fromTask(i)
////
////        val transaction = ax match {
////          case _: ReadTransactionTask2[X] =>
////            val session = DB.readOnlySession()
////            println("read")
////            new ScalikejdbcReadTransaction(session)
////          case _: ReadWriteTransactionTask2[X] =>
////            println("write")
////            DB.localTx(session => new ScalikejdbcReadWriteTransaction(session))
////        }
////
//////        val x = ax.run()
////        fromTask(Task.now("a".asInstanceOf[X]))
//      }
//    })
//  }
//
////  private def checkTransaction[T <:]()
//
//}
