package example.config.di

trait SharedExternalAdapterModules {

  val sharedExternalAdapterModules = Seq(
    new SharedExternalAdapterConfModule()
  )

}
