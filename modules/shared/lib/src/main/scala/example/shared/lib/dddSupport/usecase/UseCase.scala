package example.shared.lib.dddSupport.usecase

import scala.concurrent.ExecutionContext

trait UseCase {

  type Input
  type Output

  protected def call(arg: Input)(implicit ec: ExecutionContext): Output
}

trait EffUseCase {

  type Input
  type Output

}
