package example.shared.adapter.config.di

trait SharedSecondaryAdapterModules {

  val moduleSeq = Seq(
    new AWSClientProviderModule(),
    new ActorSystemModule(),
    new InterpreterModule()
  )

}
