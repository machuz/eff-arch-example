package example.exampleApi.usecase.user.show
import org.atnos.eff.Eff

import example.exampleApi.domain.repository.user.UserRepository

import scala.concurrent.ExecutionContext
import example.shared.lib.eff._
import javax.inject.Inject

class ShowUserUseCaseImpl @Inject()(
  userRepo: UserRepository
) extends ShowUserUseCase {
  override def execute[R: _trantask](
    arg: ShowUserUseCaseArgs
  )(
    implicit ec: ExecutionContext
  ): Eff[R, ShowUserUseCaseResult] = {
    for {
      userOpt <- userRepo.resolveById[R](arg.userId)
    } yield ShowUserUseCaseResult(userOpt)
  }

}
