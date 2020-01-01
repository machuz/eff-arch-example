package example.exampleApi.usecase.user.show

import cats.implicits._
import org.atnos.eff.Eff

import example.exampleApi.domain.repository.user.UserRepository
import example.shared.lib.dddSupport.Error.UseCaseError
import example.shared.lib.dddSupport.ErrorCode

import example.shared.lib.eff._
import example.shared.lib.eff.atnosEff._
import example.shared.lib.eff.myEff._
import javax.inject.Inject

class ShowUserUseCaseImpl @Inject()(
  userRepo: UserRepository
) extends ShowUserUseCase {
  override def execute[R: _task: _trantask: _errorEither](
    arg: ShowUserUseCaseArgs
  ): Eff[R, ShowUserUseCaseResult] = {
    for {
      userOpt <- userRepo.resolveById[R](arg.userId)
      user <- {
        fromError(userOpt match {
          case Some(user) => Either.right(user)
          case None       => Either.left(UseCaseError(ErrorCode.RESOURCE_NOT_FOUND))
        })
      }
    } yield ShowUserUseCaseResult(user)
  }

}
