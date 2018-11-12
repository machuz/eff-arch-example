package example.shared.lib.eff.either

import org.atnos.eff.{ /=, Eff, Member }

import cats.Semigroup
import example.shared.lib.dddSupport.Error
import example.shared.lib.eff._errorEither

trait ErrorEffect extends ErrorOps {
  def fromError[R, A](ea: Error Either A)(implicit member: _errorEither[R]): Eff[R, A] =
    org.atnos.eff.all.fromEither(ea.fold(Left.apply, Right.apply))
}

trait ErrorOps {

  implicit class ErrorOps[R, A](val e: Eff[R, A]) {

    def runError[U](implicit m: Member.Aux[Error Either ?, R, U]): Eff[U, Error Either A] =
      org.atnos.eff.all.runEither(e)

    def runErrorCombine[U](
      implicit m: Member.Aux[Error Either ?, R, U],
      s: Semigroup[Error]
    ): Eff[U, Error Either A] =
      org.atnos.eff.all.runEitherCombine(e)

    def catchLeftCombine(
      handle: Error => Eff[R, A]
    )(implicit member: Error Either ? /= R, s: Semigroup[Error]): Eff[R, A] =
      org.atnos.eff.all.catchLeftCombine(e)(handle)

  }

}
