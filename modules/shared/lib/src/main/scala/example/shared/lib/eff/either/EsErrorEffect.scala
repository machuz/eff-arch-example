package example.shared.lib.eff.either

import org.atnos.eff.{ /=, addon, Eff, Member }

import scalaz.{ \/, Semigroup }

import example.shared.lib.dddSupport.Error
import example.shared.lib.eff._errorEither

object ErrorEffect extends ErrorOps {
  def fromError[R, A](ea: Error \/ A)(implicit member: _errorEither[R]): Eff[R, A] =
    org.atnos.eff.all.fromEither(ea.fold(Left.apply, Right.apply))
}

trait ErrorOps {

  implicit class ErrorOps[R, A](val e: Eff[R, A]) {

    def runError[U](implicit m: Member.Aux[(Error Either ?), R, U]): Eff[U, Error \/ A] =
      addon.scalaz.either.runDisjunction(e)

    def runErrorCombine[U](
      implicit m: Member.Aux[(Error Either ?), R, U],
      s: Semigroup[Error]
    ): Eff[U, Error \/ A] =
      addon.scalaz.either.runDisjunctionCombine(e)

    def catchLeftCombine(
      handle: Error => Eff[R, A]
    )(implicit member: (Error Either ?) /= R, s: Semigroup[Error]): Eff[R, A] =
      addon.scalaz.either.catchLeftCombine(e)(handle)

  }

}
