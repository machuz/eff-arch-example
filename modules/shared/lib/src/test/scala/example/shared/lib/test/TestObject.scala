package example.shared.lib.test

import org.joda.time.LocalDate.Property
import org.joda.time.DateTimeFieldType
import org.joda.time.{ DateTime, LocalDate }

import shapeless._

import scala.language.higherKinds
import cats.data.State

trait TestObject[S, A] {
  def generate: State[S, A]
}

trait BaseTestObject[S] {
  trait TestHList[L <: HList] {
    def generate: State[S, L]
  }

  trait TestCoproduct[C <: Coproduct] {
    def generate: State[S, C]
  }
}

trait ConstantTestObject[A] extends TestObject[Unit, A]

object ConstantTestObject extends BaseTestObject[Unit] {
  type ConstTestObj[A] = ConstantTestObject[A]
  type UnitState[A]    = State[Unit, A]

  private def state[A](a: A): UnitState[A] =
    State(_ => ((), a))

  def apply[A](implicit rnd: ConstTestObj[A]): A = rnd.generate.runA(()).value

  implicit val testString: ConstTestObj[String] = new ConstTestObj[String] {
    def generate: UnitState[String] = state("string")
  }

  implicit val testInt: ConstTestObj[Int] = new ConstTestObj[Int] {
    def generate: UnitState[Int] = state(123)
  }

  implicit val testLong: ConstTestObj[Long] = new ConstTestObj[Long] {
    def generate: UnitState[Long] = state(1000L)
  }

  implicit val testDouble: ConstTestObj[Double] = new ConstTestObj[Double] {
    def generate: UnitState[Double] = state(0.5)
  }

  implicit val testBoolean: ConstTestObj[Boolean] = new ConstTestObj[Boolean] {
    def generate: UnitState[Boolean] = state(true)
  }

  implicit val testDateTime: ConstTestObj[DateTime] = new ConstTestObj[DateTime] {
    def generate: UnitState[DateTime] = state(new DateTime(2018, 3, 13, 0, 0))
  }

  implicit val testLocalDate: ConstTestObj[LocalDate] = new ConstTestObj[LocalDate] {
    def generate: UnitState[LocalDate] = state(new LocalDate(2018, 3, 13))
  }

  implicit val testProperty: ConstTestObj[Property] = new ConstTestObj[Property] {
    def generate: UnitState[Property] = state(new LocalDate(2018, 3, 13).property(DateTimeFieldType.dayOfYear()))
  }

  implicit def testOption[A: ConstTestObj]: ConstTestObj[Option[A]] = new ConstTestObj[Option[A]] {
    def generate: UnitState[Option[A]] = {
      state(Some(implicitly[ConstTestObj[A]].generate.runA(()).value))
    }
  }

  implicit def testSeq[A: ConstTestObj]: ConstTestObj[Seq[A]] = new ConstTestObj[Seq[A]] {
    override def generate: UnitState[Seq[A]] =
      state((0 until 3).map { _ =>
        implicitly[ConstTestObj[A]].generate.runA(()).value
      })
  }

  implicit def testSet[A: ConstTestObj]: ConstTestObj[Set[A]] = new ConstTestObj[Set[A]] {
    override def generate: UnitState[Set[A]] =
      state(Set(implicitly[ConstTestObj[A]].generate.runA(()).value))
  }

  implicit val testHNil: TestHList[HNil] = new TestHList[HNil] {
    def generate: UnitState[HNil] = state(HNil)
  }

  implicit def testHCons[H, T <: HList](
    implicit head: ConstTestObj[H],
    tail: TestHList[T]
  ): TestHList[H :: T] = new TestHList[H :: T] {
    def generate: UnitState[H :: T] =
      for {
        h <- head.generate
        t <- tail.generate
      } yield h :: t
  }

  implicit val testCNil: TestCoproduct[CNil] = new TestCoproduct[CNil] {
    def generate: UnitState[CNil] = throw new RuntimeException()
  }

  implicit def testCCons[H, T <: Coproduct](
    implicit inl: ConstTestObj[H],
    inr: TestCoproduct[T]
  ): TestCoproduct[H :+: T] = new TestCoproduct[H :+: T] {
    def generate: UnitState[H :+: T] = inl.generate.map(Inl(_))
  }

  implicit def testHList[A, L <: HList](
    implicit gen: Generic.Aux[A, L],
    testHList: Lazy[TestHList[L]]
  ): ConstTestObj[A] = new ConstTestObj[A] {
    def generate: UnitState[A] =
      testHList.value.generate.map(gen.from)
  }

  implicit def testCoproduct[A, C <: Coproduct](
    implicit gen: Generic.Aux[A, C],
    testCoproduct: Lazy[TestCoproduct[C]]
  ): ConstTestObj[A] = new ConstTestObj[A] {
    def generate: UnitState[A] =
      testCoproduct.value.generate.map(gen.from)
  }
}

trait DeterministicTestObject[A] extends TestObject[Int, A]

object DeterministicTestObject extends BaseTestObject[Int] {
  type DetTestObj[A] = DeterministicTestObject[A]
  type IntState[A]   = State[Int, A]

  def apply[A](implicit rnd: DetTestObj[A]): IntState[A] = rnd.generate

  implicit val testString: DetTestObj[String] = new DetTestObj[String] {
    def generate: IntState[String] = State(s => (s + 1, s"string ($s)"))
  }

  implicit val testInt: DetTestObj[Int] = new DetTestObj[Int] {
    def generate: IntState[Int] = State(s => (s + 1, s))
  }

  implicit val testLong: DetTestObj[Long] = new DetTestObj[Long] {
    def generate: IntState[Long] = State(s => (s + 1, s))
  }

  implicit val testDouble: DetTestObj[Double] = new DetTestObj[Double] {
    def generate: IntState[Double] = State(s => (s + 1, 0.1 * s))
  }

  implicit val testBoolean: DetTestObj[Boolean] = new DetTestObj[Boolean] {
    def generate: IntState[Boolean] = State(s => (s + 1, s % 2 == 0))
  }

  implicit val testDateTime: DetTestObj[DateTime] = new DetTestObj[DateTime] {
    def generate: IntState[DateTime] = State(s => (s + 1, new DateTime(2018, 3, 13, 0, 0).plusDays(s)))
  }

  implicit val testLocalDate: DetTestObj[LocalDate] = new DetTestObj[LocalDate] {
    def generate: IntState[LocalDate] = State(s => (s + 1, new LocalDate(2018, 3, 13).plusDays(s)))
  }

  implicit val testProperty: DetTestObj[Property] = new DetTestObj[Property] {
    def generate: IntState[Property] =
      State(s => (s + 1, new LocalDate(2018, 3, 13).property(DateTimeFieldType.dayOfYear())))
  }

  implicit def testOption[A: DetTestObj]: DetTestObj[Option[A]] = new DetTestObj[Option[A]] {
    def generate: IntState[Option[A]] =
      for {
        a <- implicitly[DetTestObj[A]].generate
        s <- State.get
      } yield Some(a)
  }

  implicit def testSeq[A: DetTestObj]: DetTestObj[Seq[A]] = new DetTestObj[Seq[A]] {
    override def generate: IntState[Seq[A]] =
      for {
        one   <- implicitly[DetTestObj[A]].generate
        two   <- implicitly[DetTestObj[A]].generate
        three <- implicitly[DetTestObj[A]].generate
      } yield Seq(one, two, three)
  }

  implicit def testSet[A: DetTestObj]: DetTestObj[Set[A]] = new DetTestObj[Set[A]] {
    override def generate: IntState[Set[A]] =
      for {
        one   <- implicitly[DetTestObj[A]].generate
        two   <- implicitly[DetTestObj[A]].generate
        three <- implicitly[DetTestObj[A]].generate
      } yield Set(one, two, three)
  }

  implicit val testHNil: TestHList[HNil] = new TestHList[HNil] {
    def generate: IntState[HNil] = State(s => (s + 1, HNil))
  }

  implicit def testHCons[H, T <: HList](
    implicit head: DetTestObj[H],
    tail: TestHList[T]
  ): TestHList[H :: T] = new TestHList[H :: T] {
    def generate: IntState[H :: T] = {
      for {
        h <- head.generate
        t <- tail.generate
      } yield h :: t
    }
  }

  implicit val testCNil: TestCoproduct[CNil] = new TestCoproduct[CNil] {
    def generate: IntState[CNil] = throw new RuntimeException
  }

  implicit def testCCons[H, T <: Coproduct](
    implicit inl: DetTestObj[H],
    inr: TestCoproduct[T]
  ): TestCoproduct[H :+: T] = new TestCoproduct[H :+: T] {
    def generate: IntState[H :+: T] =
      for {
        l <- inl.generate
        rOpt <- try {
          inr.generate.map(Some(_))
        } catch {
          case e: Throwable =>
            State((s: Int) => (s + 1, None))
        }
        s <- State.get
      } yield
        rOpt match {
          case Some(r) =>
            if (s % 2 == 0)
              Inl(l)
            else
              Inr(r)
          case None =>
            Inl(l)
        }

  }

  implicit def testHList[A, L <: HList](
    implicit gen: Generic.Aux[A, L],
    testHList: Lazy[TestHList[L]]
  ): DetTestObj[A] = new DetTestObj[A] {
    def generate: IntState[A] =
      testHList.value.generate.map(gen.from)
  }

  implicit def testCoproduct[A, C <: Coproduct](
    implicit gen: Generic.Aux[A, C],
    testCoproduct: Lazy[TestCoproduct[C]]
  ): DetTestObj[A] = new DetTestObj[A] {
    def generate: IntState[A] =
      testCoproduct.value.generate.map(gen.from)
  }
}
