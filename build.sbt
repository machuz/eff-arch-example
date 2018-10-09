import sbt._

lazy val allModules = Seq[ProjectReference](
  sharedExternalAdapter,
  sharedInternalAdapter,
  sharedStreamAdapter,
  sharedSecondaryAdapter,
  sharedLib,
  exampleApiExternalAdapter,
  exampleApiInternalAdapter,
  exampleApiSecondaryAdapter,
  exampleApiUseCase,
  exampleApiDomain
)

/** ***********************************************
  * root - 分離前&統合test用PJ
  * ***********************************************/
lazy val root = (project in file("."))
  .configs(Common.Settings.DebugTest)
  .settings(Common.Settings.commonSettings: _*)
  .settings(Common.Settings.commonTestSettings: _*)
  .settings(RootProject.Settings.rootSettings: _*)
  .aggregate(
    allModules: _*
  )
  .dependsOn(
    allModules.map(_ % "test->test;compile->compile;test->compile"): _*
  )
  .enablePlugins(GitVersioning, JavaServerAppPackaging, ScalafmtPlugin)

/** ***********************************************
  * shared - 共通コード
  * ***********************************************/
lazy val sharedExternalAdapter = (project in file("modules/shared/external-adapter"))
  .configs(Common.Settings.DebugTest)
  .settings(SharedProject.Settings.externalAdapterPjSettings: _*)
  .settings(libraryDependencies ++= SharedProject.Dependencies.ExternalAdapterPj.Deps)
  .dependsOn(
    sharedSecondaryAdapter % "test->test;compile->compile;test->compile",
    sharedLib              % "test->test;compile->compile;test->compile"
  )

lazy val sharedInternalAdapter = (project in file("modules/shared/internal-adapter"))
  .configs(Common.Settings.DebugTest)
  .settings(SharedProject.Settings.internalAdapterPjSettings: _*)
  .settings(libraryDependencies ++= SharedProject.Dependencies.InternalAdapterPj.Deps)
  .dependsOn(
    sharedSecondaryAdapter % "test->test;compile->compile;test->compile",
    sharedLib              % "test->test;compile->compile;test->compile"
  )

lazy val sharedStreamAdapter = (project in file("modules/shared/stream-adapter"))
  .configs(Common.Settings.DebugTest)
  .settings(SharedProject.Settings.streamAdapterPjSettings: _*)
  .settings(libraryDependencies ++= SharedProject.Dependencies.StreamAdapterPj.Deps)
  .dependsOn(
    sharedSecondaryAdapter % "test->test;compile->compile;test->compile",
    sharedLib              % "test->test;compile->compile;test->compile"
  )

lazy val sharedSecondaryAdapter = (project in file("modules/shared/secondary-adapter"))
  .configs(Common.Settings.DebugTest)
  .settings(SharedProject.Settings.secondaryAdapterPjSettings: _*)
  .settings(libraryDependencies ++= SharedProject.Dependencies.SecondaryAdapterPj.Deps)
  .dependsOn(
    sharedLib % "test->test;compile->compile;test->compile"
  )
  .enablePlugins(FlywayPlugin)

lazy val sharedLib = (project in file("modules/shared/lib"))
  .configs(Common.Settings.DebugTest)
  .settings(SharedProject.Settings.libPjSettings: _*)
  .settings(libraryDependencies ++= SharedProject.Dependencies.LibPj.Deps)
  .dependsOn()

/** ***********************************************
  * exampleApi
  * ***********************************************/
lazy val exampleApiExternalAdapter = (project in file("modules/example-api/external-adapter"))
  .configs(Common.Settings.DebugTest)
  .settings(ExampleApiProject.Settings.externalAdapterPjSettings: _*)
  .settings(libraryDependencies ++= ExampleApiProject.Dependencies.externalAdapterPjDeps)
  .dependsOn(
    sharedExternalAdapter % "test->test;compile->compile;test->compile"
  )

lazy val exampleApiInternalAdapter = (project in file("modules/example-api/internal-adapter"))
  .configs(Common.Settings.DebugTest)
  .settings(ExampleApiProject.Settings.internalAdapterPjSettings: _*)
  .settings(libraryDependencies ++= ExampleApiProject.Dependencies.internalAdapterPjDeps)
  .dependsOn(
    sharedInternalAdapter % "test->test;compile->compile;test->compile"
  )

lazy val exampleApiSecondaryAdapter = (project in file("modules/example-api/secondary-adapter"))
  .configs(Common.Settings.DebugTest)
  .settings(ExampleApiProject.Settings.secondaryAdapterPjSettings: _*)
  .settings(libraryDependencies ++= ExampleApiProject.Dependencies.secondaryAdapterPjDeps)
  .dependsOn(
    sharedSecondaryAdapter % "test->test;compile->compile;test->compile",
    exampleApiUseCase      % "test->test;compile->compile;test->compile"
  )

lazy val exampleApiUseCase = (project in file("modules/example-api/usecase"))
  .configs(Common.Settings.DebugTest)
  .settings(ExampleApiProject.Settings.useCasePjSettings: _*)
  .settings(libraryDependencies ++= ExampleApiProject.Dependencies.useCasePjDeps)
  .dependsOn(
    exampleApiDomain % "test->test;compile->compile;test->compile"
  )

lazy val exampleApiDomain = (project in file("modules/example-api/domain"))
  .configs(Common.Settings.DebugTest)
  .settings(ExampleApiProject.Settings.domainPjSettings: _*)
  .settings(libraryDependencies ++= ExampleApiProject.Dependencies.domainPjDeps)
  .dependsOn(
    sharedLib % "test->test;compile->compile;test->compile"
  )

/** ***********************************************
  * Other
  * ***********************************************/
fork in Test := false

testOptions in Test += Tests.Argument("-oT")

// disable publishing the main API jar
publishArtifact in (Compile, packageDoc) := false
// disable publishing the main sources jar
publishArtifact in (Compile, packageSrc) := false
