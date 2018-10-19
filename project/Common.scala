import com.typesafe.sbt.SbtGit.git

import com.lucidchart.sbt.scalafmt.ScalafmtCorePlugin.autoImport._

import sbt.Keys._
import sbt._
import sbtassembly.AssemblyKeys._
import sbtassembly.AssemblyPlugin.autoImport.{ assembly => _, assemblyMergeStrategy => _ }

object Common {

  private def getScalaMajorMinor(v: String): Option[String] =
    CrossVersion.partialVersion(v) match {
      case Some((2, scalaMajor)) if scalaMajor == 12 => Some("2.12")
      case Some((2, scalaMajor)) if scalaMajor == 11 => Some("2.11")
      case _                                         => None
    }

  object Settings {

    private lazy val Scala211 = "2.11.11"
    private lazy val Scala212 = "2.12.6"

    lazy val defaultScalaVersion = Scala212

    /** ***********************************************
      * common-settings
      * ***********************************************/
    lazy val commonSettings = Seq(
      organization := "io.github.ma2k8",
      scalaVersion in ThisBuild := defaultScalaVersion,
      crossScalaVersions in ThisBuild := Seq(Scala211, Scala212),
      git.formattedShaVersion := git.gitHeadCommit.value.map(_.take(8)),
      version in ThisBuild := git.gitHeadCommit.value.map(_.take(8)).getOrElse("0.1-SNAPSHOT"),
      // scalac options
      scalacOptions ++= Seq(
        "-feature",
        "-deprecation",
        "-language:existentials",
        "-language:implicitConversions",
        "-language:higherKinds",
        "-Xmax-classfile-name",
        "242",
        "-Ypartial-unification"
      ),
      fork := true,
      javaOptions in GlobalScope ++= Seq(
        s"-Duser.timezone=UTC"
      ),
      test in assembly := {},
      resolvers ++= Seq(
        "google" at "https://maven-central-asia.storage-download.googleapis.com/repos/central/data/",
        Resolver.bintrayRepo("findify", "maven"),
        Resolver.sonatypeRepo("releases"),
        Resolver.sonatypeRepo("snapshots"),
        "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
        "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
        "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
        "bintray/non" at "http://dl.bintray.com/non/maven"
      ),
      addCompilerPlugin("org.spire-math"  %% "kind-projector" % "0.9.4"),
      addCompilerPlugin("org.scalamacros" % "paradise"        % "2.1.0" cross CrossVersion.full),
      sources in doc in Compile := Seq.empty,
      sources in doc in update := Seq.empty,
      sources in doc in run := Seq.empty,
      scalafmtOnCompile := true
    )
    lazy val DebugTest = config("debug") extend Test

    // test共通設定
    lazy val commonTestSettings = Seq(
      // config for test
      javaOptions in Test ++= sys.process.javaVmArguments.filter(a => Seq("-Xmx", "-Xms", "-XX").exists(a.startsWith)),
      // output test result as xml
      testOptions in Test += Tests.Argument("-oT"),
      testOptions += Tests.Argument(s"-Duser.timezone=UTC"),
      testOptions += Tests.Argument(TestFrameworks.Specs2, "console", "junitxml"),
      fork in DebugTest := true,
      javaOptions in DebugTest ++= Seq("-agentlib:jdwp=transport=dt_socket,server=n,suspend=n,address=41230"),
      scalafmtTestOnCompile := true
    ) ++ inConfig(DebugTest)(Defaults.testTasks)

    // module testの共通設定
    lazy val commonModuleTestSettings = Seq(
      )

    /** ***********************************************
      * other-settings
      * ***********************************************/
    def defaultConf(pjName: String, adapterName: String) = {
      Seq(
        javaOptions in Test ++= Seq(
          s"-Dconfig.file=${sys.props.getOrElse("config.file", default = s"conf/partial/$pjName/$adapterName/test_h2.conf")}"
        ),
        javaOptions in run ++= Seq(
          s"-Dconfig.file=${sys.props.getOrElse("config.file", default = s"conf/partial/$pjName/$adapterName/local_h2.conf")}"
        )
      )
    }

    def confPathSettings = Seq(
      resourceDirectory in Compile := baseDirectory.value / "conf",
      resourceDirectory in Test := baseDirectory.value / "conf"
    )

    lazy val buildSettings = Seq(
//      // sbt-native-packager
//      maintainer in Docker := "Tsubasa Matsukawa <w_ma2k8@me.com>",
//      dockerBaseImage := "anapsix/alpine-java:8",
//      dockerUpdateLatest := true,
//      dockerBuildOptions in publishLocal ++= Seq("-t", "latest"),
      // aggregateしない
      aggregate in console := false,
      aggregate in run := false,
      aggregate in compile := false,
      aggregate in update := false,
      aggregate in assembly := false,
      aggregate in publish := false
    )

    lazy val subPjSettings = Seq(
      sources in (Compile, doc) := Seq.empty,
      publishArtifact in (Compile, packageDoc) := false
    )
  }

  object Dependencies {

    val AIRFRAME_VERSION = "0.68"
    lazy val diDeps = Seq(
      "org.wvlet.airframe"           %% "airframe"            % AIRFRAME_VERSION,
      "com.google.inject"            % "guice"                % "4.0",
      "com.google.inject.extensions" % "guice-assistedinject" % "4.0"
    )

    lazy val testDeps = Seq(
      "org.scalatest" %% "scalatest"  % "3.0.5"   % Test,
      "org.mockito"   % "mockito-all" % "1.10.19" % Test
    )
  }

}
