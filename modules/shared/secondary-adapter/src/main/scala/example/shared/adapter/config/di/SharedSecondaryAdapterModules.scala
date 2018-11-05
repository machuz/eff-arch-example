package example.shared.adapter.config.di

trait SharedSecondaryAdapterModules {

  val sharedSecondaryAdapterModules = Seq(
    new AWSClientProviderModule(),
    new ActorSystemModule(),
    new InterpreterModule()
  )

}
