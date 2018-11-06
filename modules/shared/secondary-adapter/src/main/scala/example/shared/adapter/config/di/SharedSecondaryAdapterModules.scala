package example.shared.adapter.config.di

trait SharedSecondaryAdapterModules {

  val sharedSecondaryAdapterModules = Seq(
    new ActorSystemModule(),
    new AWSClientProviderModule(),
    new ExecutionContextModule(),
    new InterpreterModule(),
    new SharedSecondaryAdapterConfModule()
  )

}
