import com.typesafe.sbt.SbtGit.git

import sbt.Keys._
import sbt._

object SharedProject {

  object Settings {

    lazy val externalAdapterPjSettings = {
      val pjName      = "shared"
      val adapterName = "external-adapter"
      Seq(
        name := s"$pjName-$adapterName",
        parallelExecution in Test := false
      ) ++
      Common.Settings.defaultConf(pjName, adapterName) ++
      Common.Settings.commonSettings ++
      Common.Settings.commonTestSettings ++
      Common.Settings.confPathSettings
    }

    lazy val internalAdapterPjSettings = {
      val pjName      = "shared"
      val adapterName = "internal-adapter"
      Seq(
        name := s"$pjName-$adapterName",
        parallelExecution in Test := false
      ) ++
      Common.Settings.defaultConf(pjName, adapterName) ++
      Common.Settings.commonSettings ++
      Common.Settings.commonTestSettings ++
      Common.Settings.confPathSettings
    }

    lazy val streamAdapterPjSettings = {
      val pjName      = "shared"
      val adapterName = "stream-adapter"
      Seq(
        name := s"$pjName-$adapterName",
        parallelExecution in Test := false
      ) ++
      Common.Settings.defaultConf(pjName, adapterName) ++
      Common.Settings.commonSettings ++
      Common.Settings.commonTestSettings ++
      Common.Settings.confPathSettings
    }

    lazy val secondaryAdapterPjSettings = {
      val pjName      = "shared"
      val adapterName = "secondary-adapter"
      Seq(
        name := s"$pjName-$adapterName",
        parallelExecution in Test := false
      ) ++
      Common.Settings.defaultConf(pjName, adapterName) ++
      Common.Settings.commonSettings ++
      Common.Settings.commonTestSettings ++
      Common.Settings.confPathSettings
    }

    lazy val libPjSettings = Seq(
      name := """shared-lib""",
      parallelExecution in Test := true,
      fork in Test := true
    ) ++
    Common.Settings.commonSettings ++
    Common.Settings.commonTestSettings ++
    Seq(
      // In order to get Git Commit ID from Scala source
      sourceGenerators in Compile += Def.task {
        CommitID.createCommitIdCode(
          (sourceManaged in Compile).value,
          git.gitHeadCommit.value
        )
      }.taskValue
    )
  }

  object Dependencies {

    object ExternalAdapterPj {
      lazy val Deps = Seq()
    }

    object InternalAdapterPj {
      lazy val Deps = grpcServerDeps

      lazy val grpcServerDeps = Seq(
//        "io.grpc" % "grpc-all"      % grpcJavaVersion,
//        "io.grpc" % "grpc-netty"    % com.trueaccord.scalapb.compiler.Version.grpcJavaVersion,
//        "io.grpc" % "grpc-services" % grpcJavaVersion
      )

    }

    object StreamAdapterPj {
      lazy val Deps = KinesisConsumerDeps

      lazy val KinesisConsumerDeps = Seq(
        "software.amazon.kinesis" % "amazon-kinesis-client" % "2.0.1"
      )
    }

    object SecondaryAdapterPj {
      lazy val Deps =
//          grpcDeps ++
      jwtDeps ++
      dbDeps ++
      redisDeps ++
      awsDeps ++
      playJsonDeps ++
      mailDeps ++
      wsDeps ++
      httpDeps ++
      Common.Dependencies.testDeps

      lazy val grpcDeps = Seq(
//        "com.trueaccord.scalapb" %% "scalapb-runtime"      % scalapbVersion % "protobuf",
//        "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % scalapbVersion
      )

      lazy val jwtDeps = Seq(
        )

      lazy val dbDeps = Seq(
        "com.h2database"     % "h2"                     % "1.4.193",
        "mysql"              % "mysql-connector-java"   % "5.1.36",
        "com.typesafe.slick" %% "slick-hikaricp"        % "3.2.2",
        "com.typesafe.play"  %% "play-slick"            % "3.0.0",
        "com.typesafe.play"  %% "play-slick-evolutions" % "3.0.0"
      )

      lazy val redisDeps = Seq(
//        "net.debasishg" %% "redisclient" % "3.0"
      )

      lazy val akkaDeps = Seq(
        "com.typesafe.akka" %% "akka-actor" % "2.5.4",
        "com.typesafe.akka" %% "akka-slf4j" % "2.5.4"
      )

      lazy val awsDeps = Seq(
        "com.amazonaws"          % "aws-java-sdk-ses"      % "1.11.399",
        "com.amazonaws"          % "aws-java-sdk-dynamodb" % "1.11.399",
        "com.amazonaws"          % "aws-java-sdk-kinesis"  % "1.11.399",
        "com.amazonaws"          % "aws-java-sdk-s3"       % "1.11.399",
        "software.amazon.awssdk" % "regions"               % "2.0.1"
      )

      lazy val playJsonDeps = Seq(
        "net.sf.json-lib" % "json-lib"              % "2.4" classifier "jdk15",
        "ai.x"            %% "play-json-extensions" % "0.10.0"
      )

      lazy val mailDeps = Seq(
        "eu.medsea.mimeutil" % "mime-util" % "2.1.3" exclude ("org.slf4j", "slf4j-log4j12")
      )

      val shttpVer = "1.1.4"
      lazy val httpDeps = Seq(
        "com.softwaremill.sttp" %% "core"                             % shttpVer,
        "com.softwaremill.sttp" %% "async-http-client-backend-monix"  % shttpVer,
        "com.softwaremill.sttp" %% "async-http-client-handler-scalaz" % "0.0.13"
      )

      lazy val wsDeps = Seq(
        "com.typesafe.play" %% "play-ahc-ws-standalone"  % "1.1.3",
        "com.typesafe.play" %% "play-ws-standalone-json" % "1.1.3",
        "com.typesafe.play" %% "play-ws-standalone-xml"  % "1.1.3"
      )
    }

    object LibPj {
      lazy val Deps =
      excelDeps ++
      circeDeps ++
      playJsonDeps ++
      playIterateeDeps ++
      utilsDeps ++
      loggingDeps ++
      Common.Dependencies.testDeps ++
      dbTestDeps ++
      redisDeps ++
      monixDeps ++
      effDeps ++
      Common.Dependencies.diDeps

      val monixVer = "2.3.2"
      lazy val monixDeps = Seq(
        "io.monix" %% "monix"           % monixVer,
        "io.monix" %% "monix-scalaz-72" % monixVer
      )

      val effVer = "5.2.0"
      lazy val effDeps = Seq(
        "org.atnos" %% "eff"        % effVer,
        "org.atnos" %% "eff-scalaz" % effVer,
        "org.atnos" %% "eff-monix"  % effVer
      )

      val scalazVer = "7.2.15"
      lazy val utilsDeps = Seq(
        "org.scala-lang"         % "scala-library"              % Common.Settings.defaultScalaVersion,
        "org.scalaz"             %% "scalaz-core"               % scalazVer,
        "org.scalaz"             %% "scalaz-scalacheck-binding" % scalazVer % Test,
        "com.codecommit"         %% "shims"                     % "1.2.1",
        "com.github.scopt"       %% "scopt"                     % "3.5.0",
        "com.eaio.uuid"          % "uuid"                       % "3.2",
        "com.github.nscala-time" %% "nscala-time"               % "2.14.0",
        "org.codehaus.janino"    % "janino"                     % "2.6.1",
        "com.jsuereth"           %% "scala-arm"                 % "2.0",
        "com.iheart"             %% "ficus"                     % "1.4.3",
        "eu.bitwalker"           % "UserAgentUtils"             % "1.18",
        "joda-time"              % "joda-time"                  % "2.9.4"
      )

      lazy val loggingDeps = Seq(
        "com.typesafe.scala-logging" %% "scala-logging"  % "3.9.0",
        "ch.qos.logback"             % "logback-classic" % "1.1.3",
        "io.sentry"                  % "sentry-logback"  % "1.7.5"
      )

      lazy val playJsonDeps = Seq(
        "com.typesafe.play" %% "play-json"      % "2.6.3",
        "com.typesafe.play" %% "play-json-joda" % "2.6.3"
      )

      val circeVer = "0.9.3"
      lazy val circeDeps = Seq(
        "io.circe"      %% "circe-core"           % circeVer,
        "io.circe"      %% "circe-generic"        % circeVer,
        "io.circe"      %% "circe-generic-extras" % circeVer,
        "io.circe"      %% "circe-parser"         % circeVer,
        "io.circe"      %% "circe-java8"          % circeVer,
        "com.pauldijou" %% "jwt-circe"            % "0.16.0"
      )

      lazy val playIterateeDeps = Seq(
        "com.typesafe.play" %% "play-iteratees"                  % "2.6.1",
        "com.typesafe.play" %% "play-iteratees-reactive-streams" % "2.6.1"
      )

      lazy val excelDeps = Seq(
        "info.folone" %% "poi-scala" % "0.18"
      )

      lazy val redisDeps = Seq(
        "com.github.etaty" %% "rediscala" % "1.8.0"
      )

      lazy val dbTestDeps = Seq(
        // TOOD: DBIO型のため
        "com.typesafe.slick"   %% "slick"             % "3.2.2",
        "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0"
      )
    }
  }

  object CommitID {
    def createCommitIdCode(base: File, commitIDOpt: Option[String]): Seq[File] = {
      val output   = base / "example" / "shared" / "lib" / "commit" / "CommitID.scala"
      val commitId = commitIDOpt.getOrElse("No Commit ID")

      val commiIdObj =
        s"""/**
           |  * This file was generated by `project/Common.scala`.
           |  * So you must not edit this file.
           |  */
           |
           |package example.shared.lib.commit
           |
           |object CommitID {
           |  val getCommitID: String = "$commitId"
           |}
      """.stripMargin

      IO.write(output, commiIdObj)
      Seq(output)
    }
  }

}
