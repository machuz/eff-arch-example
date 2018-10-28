package example.exampleApi.usecase.user.show

import org.atnos.eff.Eff

import example.exampleApi.domain.model.user.{ User, UserId }
import example.shared.lib.dddSupport.usecase.{ EffPushPort, EffUseCase, UseCaseArgument, UseCaseResult }
import example.shared.lib.dddSupport.Error
import example.shared.lib.eff._

import scala.concurrent.ExecutionContext

abstract class ShowUserUseCase extends EffUseCase with EffPushPort[ShowUserUseCaseArgs, Error, ShowUserUseCaseResult] {

  def execute[R: _trantask](
    arg: ShowUserUseCaseArgs
  )(implicit ec: ExecutionContext): Eff[R, ShowUserUseCaseResult]

}

case class ShowUserUseCaseArgs(
  userId: UserId
) extends UseCaseArgument

case class ShowUserUseCaseResult(userOpt: Option[User]) extends UseCaseResult
