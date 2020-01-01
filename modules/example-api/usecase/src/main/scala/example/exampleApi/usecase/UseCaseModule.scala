package example.exampleApi.usecase

import com.google.inject.AbstractModule

import example.exampleApi.usecase.user.create.{ CreateUserUseCase, CreateUserUseCaseImpl }
import example.exampleApi.usecase.user.show.{ ShowUserUseCase, ShowUserUseCaseImpl }

class UseCaseModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[ShowUserUseCase]).to(classOf[ShowUserUseCaseImpl])
    bind(classOf[CreateUserUseCase]).to(classOf[CreateUserUseCaseImpl])
  }

}
