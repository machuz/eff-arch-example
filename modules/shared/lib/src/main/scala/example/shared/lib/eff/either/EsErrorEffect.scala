package example.shared.lib.eff.either

import org.atnos.eff.{ /=, addon, Eff, Member }

import scalaz.{ \/, Semigroup }

import jp.eigosapuri.es.shared.lib.dddSupport.EsError
import jp.eigosapuri.es.shared.lib.eff._errorEither

object EsErrorEffect extends EsErrorOps {
  def fromEsError[R, A](ea: EsError \/ A)(implicit member: _errorEither[R]): Eff[R, A] =
    org.atnos.eff.all.fromEither(ea.fold(Left.apply, Right.apply))
}

trait EsErrorOps {

  implicit class EsErrorOps[R, A](val e: Eff[R, A]) {

    def runEsError[U](implicit m: Member.Aux[(EsError Either ?), R, U]): Eff[U, EsError \/ A] =
      addon.scalaz.either.runDisjunction(e)

    def runEsErrorCombine[U](
      implicit m: Member.Aux[(EsError Either ?), R, U],
      s: Semigroup[EsError]
    ): Eff[U, EsError \/ A] =
      addon.scalaz.either.runDisjunctionCombine(e)

    def catchLeftCombine(
      handle: EsError => Eff[R, A]
    )(implicit member: (EsError Either ?) /= R, s: Semigroup[EsError]): Eff[R, A] =
      addon.scalaz.either.catchLeftCombine(e)(handle)

  }

}
