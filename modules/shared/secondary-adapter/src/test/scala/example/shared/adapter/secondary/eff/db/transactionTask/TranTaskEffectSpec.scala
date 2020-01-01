package example.shared.adapter.secondary.eff.db.transactionTask

import com.google.inject.Guice

import org.atnos.eff.{ Eff, Fx }
import org.scalatest.{ BeforeAndAfter }

import example.shared.adapter.config.di.SharedSecondaryAdapterModules
import example.shared.adapter.secondary.eff.rdb.scalikejdbc.{ ScalikejdbcDbSession, TranTaskEffect }
import example.shared.lib.eff.ErrorEither
import example.shared.lib.eff.db.transactionTask.{ TransactionTask, TransactionTaskInterpreter }
import example.shared.lib.test.{ AbstractSpecification, DeterministicTestObject }
import example.shared.lib.eff.myEff._
import example.shared.lib.eff.atnosEffSyntax._
import example.shared.lib.eff.atnosEff._
import example.shared.adapter.secondary.rdb.scalikejdbc.pimp.RichMySQLSyntaxSupport._
import example.shared.lib.dddSupport
import example.shared.lib.dddSupport.Error.DatabaseError
import monix.eval.Task
import scalikejdbc.{ GlobalSettings, LoggingSQLAndTimeSettings }

class TranTaskEffectSpec
  extends AbstractSpecification
  with BeforeAndAfter
  with TranTaskEffect
  with SharedSecondaryAdapterModules {

  implicit val int =
    Guice.createInjector(sharedSecondaryAdapterModules: _*).getInstance(classOf[TransactionTaskInterpreter])

  GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
    enabled = false,
    logLevel = 'WARN,
    warningEnabled = false,
    warningThresholdMillis = 1000L,
    warningLogLevel = 'WARN
  )

  import org.mockito.Mockito._
  import org.mockito.ArgumentMatchers._
  import scalikejdbc._

  before {
    implicit val as: DBSession = AutoSession
    sql"CREATE TABLE IF NOT EXISTS `pp_test_table` (`id` varchar(50) PRIMARY KEY, `name` text)"
      .execute()
      .apply()
  }

  after {
    implicit val as: DBSession = AutoSession
    sql"DROP TABLE IF EXISTS `pp_test_table`".execute().apply()
  }

  trait SetUp {

    val (_, (testData1, testData2, testData3)) = (for {
      testData1 <- DeterministicTestObject[PpTestTable]
      testData2 <- DeterministicTestObject[PpTestTable]
      testData3 <- DeterministicTestObject[PpTestTable]
    } yield {
      (
        testData1,
        testData2,
        testData3
      )
    }).run(0).value
    type R = Fx.fx3[TransactionTask, ErrorEither, Task]
    implicit val scheduler = monix.execution.Scheduler.global
    implicit val ec        = scala.concurrent.ExecutionContext.Implicits.global
  }

  "TranTaskEffect" should {

    "runRead" must {
      "some" in new SetUp {
        val prepareTestData = await((for {
          res <- addTestData[R](testData1)
        } yield res).runReadWriteTranTask.runError.runAsync.runToFuture)

        val actual = await((for {
          res <- selectTestData[R](testData1.id)
        } yield res).runReadTranTask.runError.runAsync.runToFuture)

        val expected = Right(Option(testData1))

        actual must be(expected)
      }

      "none" in new SetUp {
        val actual = await((for {
          res <- selectTestData[R]("hogehoge")
        } yield res).runReadTranTask.runError.runAsync.runToFuture)

        val expected = Right(None)

        actual mustBe expected
      }

      "failed(either)" in new SetUp {
        val actual = await((for {
          _   <- fromError[R, Unit](Left(DatabaseError(new RuntimeException(""))))
          res <- selectTestData[R](testData1.id)
        } yield res).runReadTranTask.runError.runAsync.runToFuture)

        actual.isLeft mustBe true
      }

      "failed(throw in scalikejdbc)" in new SetUp {
        val actual = await((for {
          _   <- throwErrorInScalijeJdbc[R]
          res <- selectTestData[R](testData1.id)
        } yield res).runReadTranTask.runError.runAsync.runToFuture)

        actual.isLeft mustBe true
      }

      "failed(throw in task)" in new SetUp {
        val actual: Either[dddSupport.Error, Option[PpTestTable]] = await((for {
          res <- selectTestData[R](testData1.id)
          _   <- fromTask[R, Unit](Task.delay(throw new RuntimeException("test error")))
        } yield res).runReadTranTask.runError.runAsync.runToFuture)

        actual.isLeft mustBe true
      }

    }

    "runReadWrite" must {

      "failed transaction(either)" in new SetUp {
        val actual = await((for {
          _   <- addTestData[R](testData1)
          _   <- fromError[R, Unit](Left(DatabaseError(new RuntimeException(""))))
          res <- selectTestData[R](testData1.id)
        } yield res).runReadWriteTranTask.runError.runAsync.runToFuture)

        actual.isLeft mustBe true

        val rollbackCheck = await((for {
          res <- selectTestData[R](testData1.id)
        } yield res).runReadTranTask.runError.runAsync.runToFuture)

        rollbackCheck mustBe Right(None)
      }

      "failed transaction(throw in scalikejdbc)" in new SetUp {

        val actual = await((for {
          _   <- addTestData[R](testData1)
          res <- selectTestData[R](testData1.id)
          _ <- {
            throwErrorInScalijeJdbc[R]
          }
        } yield res).runReadWriteTranTask.runError.runAsync.runToFuture)

        actual.isLeft mustBe true

        val rollbackCheck = await((for {
          res <- selectTestData[R](testData1.id)
        } yield res).runReadTranTask.runError.runAsync.runToFuture)

        rollbackCheck mustBe Right(None)
      }

      "failed transaction(throw in task)" in new SetUp {

        val actual = await(
          (for {
            _   <- addTestData[R](testData1)
            res <- selectTestData[R](testData1.id)
            _   <- fromTask[R, Unit](Task.delay((None: Option[Int]).get))
          } yield res).runReadWriteTranTask.runError.runAsync.runToFuture
        )

        actual.isLeft mustBe true

        val rollbackCheck = await((for {
          res <- selectTestData[R](testData1.id)
        } yield res).runReadTranTask.runError.runAsync.runToFuture)

        rollbackCheck mustBe Right(None)
      }

      "failed transaction(raw exception after trantask)" in new SetUp {

        val actual = await(
          (for {
            _   <- addTestData[R](testData1)
            res <- selectTestData[R](testData1.id)
            _   <- Eff.pure[R, Int] { (None: Option[Int]).get }
          } yield res).runReadWriteTranTask.runError.runAsync.runToFuture
        )
        actual.isLeft mustBe true

        val rollbackCheck = await((for {
          res <- selectTestData[R](testData1.id)
        } yield res).runReadTranTask.runError.runAsync.runToFuture)
        rollbackCheck mustBe Right(None)
      }

      "failed transaction(raw exception before trantask)" in new SetUp {

        // Task or TransactionTaskの文脈に包まれる前に生Exceptionが走るとNG
        // しかし後続のテストが成功するのでコネクションは開放されていることがわかる
        intercept[NoSuchElementException] {
          await(
            (for {
              _   <- Eff.pure[R, Int] { (None: Option[Int]).get }
              _   <- addTestData[R](testData1)
              res <- selectTestData[R](testData1.id)
            } yield res).runReadWriteTranTask.runError.runAsync.runToFuture
          )
        }

        val rollbackCheck = await((for {
          res <- selectTestData[R](testData1.id)
        } yield res).runReadTranTask.runError.runAsync.runToFuture)
        rollbackCheck mustBe Right(None)
      }

      "success transaction" in new SetUp {
        val actual = await((for {
          _  <- addTestData[R](testData1)
          _  <- addTestData[R](testData2)
          _  <- addTestData[R](testData3)
          h1 <- selectTestData[R](testData1.id)
          h2 <- selectTestData[R](testData2.id)
          h3 <- selectTestData[R](testData3.id)
        } yield h3).runReadWriteTranTask.runError.runAsync.runToFuture)

        val expected = Right(Option(testData3))

        actual mustBe expected
      }

    }

  }

  def selectTestData[R: _trantask](id: String): Eff[R, Option[PpTestTable]] = {
    val ppt = PpTestTable.syntax("ptt")
    val res = ScalikejdbcDbSession.sessionAsk.map { implicit session =>
      withSQL[PpTestTable] {
        select
          .from(PpTestTable as ppt)
          .where
          .eq(ppt.id, id)
      }.map(PpTestTable(ppt.resultName)).single.apply
    }
    fromTranTask(res)
  }

  def addTestData[R: _trantask](v: PpTestTable): Eff[R, PpTestTable] = {
    val res = ScalikejdbcDbSession.sessionAsk.map { implicit session =>
      withSQL {
        val col = PpTestTable.column
        insert
          .into(PpTestTable)
          .namedValues(
            col.id   -> v.id,
            col.name -> v.name
          )
          .onDuplicateKeyUpdate(
            col.id   -> v.id,
            col.name -> v.name
          )
      }.update.apply
      v
    }
    fromTranTask(res)
  }

  def removeTestData[R: _trantask](id: String): Eff[R, Unit] = {
    val res = ScalikejdbcDbSession.sessionAsk
      .map { implicit session =>
        withSQL {
          val col = PpTestTable.column
          delete.from(PpTestTable).where.eq(col.id, id)
        }.update.apply
      }
      .map(_ => ())
    fromTranTask(res)
  }

  def throwErrorInScalijeJdbc[R: _trantask]: Eff[R, Unit] = {
    val res = ScalikejdbcDbSession.sessionAsk
      .map { implicit session =>
        withSQL {
          throw new RuntimeException("error")
        }.update.apply
      }
      .map(_ => ())
    fromTranTask(res)
  }

  case class PpTestTable(
    id: String,
    name: String
  )

  object PpTestTable extends SQLSyntaxSupport[PpTestTable] {
    override val schemaName: Option[String] = None
    override val tableName: String          = "pp_test_table"

    def apply(syn: SyntaxProvider[PpTestTable])(rs: WrappedResultSet): PpTestTable =
      apply(syn.resultName)(rs)

    def apply(rn: ResultName[PpTestTable])(rs: WrappedResultSet): PpTestTable =
      PpTestTable(id = rs.string(rn.id), name = rs.string(rn.name))
  }

}
