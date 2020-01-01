package example.exampleApi.usecase.user.create

import org.atnos.eff.Eff

import example.exampleApi.domain.model.user.User
import example.exampleApi.domain.repository.user.UserRepository
import example.shared.lib.eff.myEff._
import example.shared.lib.eff.atnosEff._
import javax.inject.Inject

class CreateUserUseCaseImpl @Inject()(
  userRepo: UserRepository
) extends CreateUserUseCase {
  override def execute[R: _task: _trantask: _idgen: _clockm](
    arg: CreateUserUseCaseArgs
  ): Eff[R, CreateUserUseCaseResult] = {
    for {
      user <- User.applyEff[R](arg.name)
      _    <- userRepo.store[R](user)
    } yield CreateUserUseCaseResult(user)
  }

}
