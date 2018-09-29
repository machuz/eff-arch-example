import sbt.Keys._
import sbt._
import sbtassembly.AssemblyPlugin.autoImport.{assembly => _, assemblyMergeStrategy => _}

object RootProject {

  object Settings {
    lazy val rootSettings =
      Seq(
        name := """example""",
        parallelExecution in Test := true
      ) ++
//        Common.Settings.defaultConf("aggregate-server","") ++
        Common.Settings.commonSettings ++
        Common.Settings.commonTestSettings ++
        Common.Settings.buildSettings
  }

  object Dependencies {

  }

}
