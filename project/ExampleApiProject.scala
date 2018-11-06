import sbt._
import sbt.Keys._
object ExampleApiProject {

  object Settings {

    lazy val externalAdapterPjSettings = {
      val pjName      = "example-api"
      val adapterName = "external-adapter"
      Seq(
        name := s"$pjName-$adapterName",
        parallelExecution in Test := true
      ) ++
      Common.Settings.defaultConf(pjName, adapterName) ++
//        Common.Settings.playSettings ++
      Common.Settings.buildSettings ++
      Common.Settings.commonSettings ++
      Common.Settings.commonTestSettings ++
      Common.Settings.confPathSettings
    }

    val internalAdapterPjSettings = {
      val pjName      = "example-api"
      val adapterName = "internal-adapter"
      Seq(
        name := s"$pjName-$adapterName",
        parallelExecution in Test := true
      ) ++
      Common.Settings.defaultConf(pjName, adapterName) ++
//        Common.Settings.grpcSettings ++
      Common.Settings.buildSettings ++
      Common.Settings.confPathSettings ++
      Common.Settings.commonSettings ++
      Common.Settings.commonTestSettings
    }

    val secondaryAdapterPjSettings = {
      val pjName      = "example-api"
      val adapterName = "secondary-adapter"
      Seq(
        name := s"$pjName-$adapterName",
        parallelExecution in Test := false,
      ) ++
      Common.Settings.defaultConf(pjName, adapterName) ++
      Common.Settings.confPathSettings ++
      Common.Settings.commonSettings ++
      Common.Settings.commonTestSettings
    }

    val useCasePjSettings = Seq(
      name := """example-api-usecase""",
      parallelExecution in Test := true,
      fork in Test := true
    ) ++
    Common.Settings.commonSettings ++
    Common.Settings.commonTestSettings

    val domainPjSettings = Seq(
      name := """example-api-domain""",
      parallelExecution in Test := true,
      fork in Test := true
    ) ++
    Common.Settings.commonSettings ++
    Common.Settings.commonTestSettings

  }

  object Dependencies {
    lazy val externalAdapterPjDeps = Seq()

    lazy val internalAdapterPjDeps = Seq()

    lazy val secondaryAdapterPjDeps = Seq()

    lazy val useCasePjDeps = Seq()

    lazy val domainPjDeps = Seq()

  }
}
