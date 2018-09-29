package example.shared.lib.dddSupport.usecase

import org.atnos.eff.Eff

import example.shared.lib.dddSupport.Error

trait EffPushPort[Arg <: UseCaseArgument, ERR <: Error, Result <: UseCaseResult] { self: EffUseCase =>

  type This <: PushPort[Arg, Result]

  override final type Input = Arg

  override final type Output = Eff[_, Result]

}
