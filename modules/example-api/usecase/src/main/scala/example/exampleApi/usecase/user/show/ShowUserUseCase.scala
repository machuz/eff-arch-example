package example.exampleApi.usecase.user.show

import org.atnos.eff.Eff

import example.exampleApi.domain.model.user.{ User, UserId }
import example.shared.lib.dddSupport.usecase.{ EffPushPort, EffUseCase, UseCaseArgument, UseCaseResult }
import example.shared.lib.dddSupport.Error
import example.shared.lib.eff._errorEither
import example.shared.lib.eff.myEff._
import example.shared.lib.eff.atnosEff._

abstract class ShowUserUseCase extends EffUseCase with EffPushPort[ShowUserUseCaseArgs, Error, ShowUserUseCaseResult] {

  def execute[R: _task: _trantask: _errorEither](
    arg: ShowUserUseCaseArgs
  ): Eff[R, ShowUserUseCaseResult]
}

case class ShowUserUseCaseArgs(
  userId: UserId
) extends UseCaseArgument

case class ShowUserUseCaseResult(user: User) extends UseCaseResult
