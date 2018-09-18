package example.shared.lib.dddSupport.usecase

import scala.concurrent.ExecutionContext

trait PushPort[Arg, Result] { self: UseCase =>

  override final type Input = Arg

  override final type Output = Result

  def execute(arg: Arg)(implicit ec: ExecutionContext): Result =
    call(arg)
}
