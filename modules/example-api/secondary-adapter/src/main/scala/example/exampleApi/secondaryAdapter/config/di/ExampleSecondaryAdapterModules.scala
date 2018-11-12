package example.exampleApi.secondaryAdapter.config.di
import example.exampleApi.usecase.UseCaseModule

trait ExampleSecondaryAdapterModules {

  val exampleSecondaryAdapterModules = Seq(
    new UseCaseModule(),
    new RepositoryModule()
  )

}
