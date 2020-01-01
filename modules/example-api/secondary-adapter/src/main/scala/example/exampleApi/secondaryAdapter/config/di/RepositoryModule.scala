package example.exampleApi.secondaryAdapter.config.di

import com.google.inject.AbstractModule

import example.exampleApi.domain.repository.user.UserRepository
import example.exampleApi.secondaryAdapter.repository.user.UserRepositoryImpl

class RepositoryModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[UserRepository]).to(classOf[UserRepositoryImpl])
  }

}
