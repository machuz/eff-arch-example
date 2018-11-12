package example.config.di

trait SharedExternalAdapterModules {

  val sharedExternalAdapterModules = Seq(
    new AkkaHttpServerBuiltInModule(),
    new SharedExternalAdapterConfModule()
  )

}
