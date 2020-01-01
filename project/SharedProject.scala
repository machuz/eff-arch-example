
import com.typesafe.sbt.SbtGit.git

import sbt.Keys._
import sbt.{ Def, _ }

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
        parallelExecution in Test := false,
        scalacOptions ++= Seq(
          "-unchecked",
          "-language:_",
          "-target:jvm-1.8",
          "-encoding",
          "UTF-8"
        )
      ) ++
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
        Common.Settings.commonSettings ++
        Common.Settings.commonTestSettings ++
        Common.Settings.confPathSettings
    }

    lazy val sparkAdapterPjSettings = {
      val pjName      = "shared"
      val adapterName = "spark-adapter"

      lazy val sparkSettings = Seq(
      )
      Seq(
        name := s"$pjName-$adapterName",
        parallelExecution in Test := false,
//        crossScalaVersions := Seq(Common.Settings.Scala211, Common.Settings.Scala212)
      ) ++
        Common.Settings.defaultConf(pjName, adapterName) ++
        Common.Settings.commonSettings ++
        Common.Settings.commonTestSettings ++
        Common.Settings.confPathSettings
    }

    lazy val batchAdapterPjSettings = {
      val pjName      = "shared"
      val adapterName = "batch-adapter"
      Seq(
        name := s"$pjName-$adapterName",
        parallelExecution in Test := false
      ) ++
        Common.Settings.commonSettings ++
        Common.Settings.commonTestSettings ++
//        Common.Settings.multiMainClassPjSettings ++
        Common.Settings.confPathSettings
    }

    lazy val secondaryAdapterPjSettings = {
      val pjName      = "shared"
      val adapterName = "secondary-adapter"
      // ※これを入れておかないと、インターナルAPIがDNSを解決できなくなるエラーが起きる。
      System.setProperty("io.grpc.internal.DnsNameResolverProvider.enable_jndi", "false")
      import java.security.Security
      Security.setProperty("networkaddress.cache.ttl", "5")
      Security.setProperty("networkaddress.cache.negative.ttl", "0")
      // ※ここまで
      Seq(
        name := s"$pjName-$adapterName",
        parallelExecution in Test := false
      ) ++
        Common.Settings.defaultConf(pjName, adapterName) ++
        Common.Settings.commonSettings ++
        Common.Settings.commonTestSettings ++
        Common.Settings.confPathSettings
//        scalaPbSettings
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

    lazy val domainPjSettings = Seq(
      name := """shared-domain""",
      parallelExecution in Test := true,
      fork in Test := true
    ) ++
      Common.Settings.commonSettings ++
      Common.Settings.commonTestSettings

//    lazy val scalaPbSettings =
//      PB.targets in Compile := Seq(
//        protoc_lint.ProtocLint() -> (sourceManaged in Compile).value,
//        scalapb.gen()            -> (sourceManaged in Compile).value
//      )
  }

  object Dependencies {

    object ExternalAdapterPj {
      lazy val Deps = akkaDeps ++ circeDeps

      val akkaVer     = "2.5.25"
      val akkaHttpVer = "10.1.9"
      lazy val akkaDeps = Seq(
        "com.typesafe.akka" %% "akka-http"         % akkaHttpVer,
        "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVer % Test,
        "com.typesafe.akka" %% "akka-testkit"      % akkaVer % Test,
        "com.typesafe.akka" %% "akka-actor"        % akkaVer,
        "com.typesafe.akka" %% "akka-slf4j"        % akkaVer,
        "ch.megard"         %% "akka-http-cors"    % "0.4.1"
      )

      lazy val circeDeps = Seq(
        "de.heikoseeberger" %% "akka-http-circe"              % "1.27.0",
//        "io.tabmo"          %% "circe-validation-core"        % "0.0.6",
//        "io.tabmo"          %% "circe-validation-extra-rules" % "0.0.6"
      )

    }

    object InternalAdapterPj {
      lazy val Deps = grpcServerDeps ++ loggingDeps

      lazy val grpcServerDeps = Seq(
//        "io.grpc"              % "grpc-all"              % grpcJavaVersion exclude ("io.netty", "netty-codec"),
//        "io.grpc"              % "grpc-services"         % grpcJavaVersion,
//        "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapbVersion
      )

      lazy val loggingDeps = Seq(
        "org.slf4j" % "jul-to-slf4j" % "1.7.28"
      )

    }

    object StreamAdapterPj {
      lazy val Deps = KinesisConsumerDeps

      lazy val KinesisConsumerDeps = Seq(
        "software.amazon.kinesis" % "amazon-kinesis-client" % "2.2.0"
      )
    }

    object SparkAdapterPj {
      lazy val Deps =
        SparkDeps ++
          SecondaryAdapterPj.dbDeps ++
          LibPj.utilsDeps ++
          LibPj.loggingDeps ++
          LibPj.monixDeps ++
          LibPj.effDeps ++
          LibPj.testDeps ++
          LibPj.diDeps ++
          LibPj.circeForSparkDeps

      lazy val sparkVersion = "2.4.2"
      lazy val SparkDeps = Seq(
        "org.apache.spark" %% "spark-core"      % sparkVersion,
        "org.apache.spark" %% "spark-sql"       % sparkVersion,
        "org.apache.spark" %% "spark-streaming" % sparkVersion % "provided",
        "org.apache.spark" %% "spark-repl"      % sparkVersion % "provided",
        "org.apache.spark" %% "spark-mllib"     % sparkVersion % "runtime"
      )

    }

    object SecondaryAdapterPj {
      lazy val Deps =
      //          grpcDeps ++
        jwtDeps ++
          dbDeps ++
          awsDeps ++
          //      mailDeps ++
          httpDeps ++
          redisDeps ++
          excelDeps ++
          akkaDeps ++
          scalaPBDeps ++
          otherDeps

      lazy val otherDeps = Seq(
        "com.jcraft"         % "jsch"                  % "0.1.55",
        "org.apache.commons" % "commons-compress"      % "1.19",
        "com.sendgrid"       % "sendgrid-java"         % "4.4.1",
        "com.dimafeng"       %% "testcontainers-scala" % "0.32.0" % Runtime
      )

      lazy val grpcDeps = Seq(
//        "com.trueaccord.scalapb" %% "scalapb-runtime"      % scalapbVersion % "protobuf",
//        "com.trueaccord.scalapb" %% "scalapb-runtime-grpc" % scalapbVersion
      )

      lazy val jwtDeps = Seq(
      )

      lazy val dbDeps = Seq(
        "com.h2database"  % "h2"                                % "1.4.199" % Test,
        "mysql"           % "mysql-connector-java"              % "8.0.17",
        "org.scalikejdbc" %% "scalikejdbc-syntax-support-macro" % "3.3.5",
        "org.scalikejdbc" %% "scalikejdbc"                      % "3.3.5",
        "org.scalikejdbc" %% "scalikejdbc-config"               % "3.3.5",
        "com.zaxxer"      % "HikariCP"                          % "3.4.1",
        "org.flywaydb"    % "flyway-core"                       % "5.2.4"
      )

      lazy val akkaDeps = Seq(
        "com.typesafe.akka" %% "akka-stream" % ExternalAdapterPj.akkaVer
      )

      val awsVersion = "1.11.598"
      lazy val awsDeps = Seq(
        "com.amazonaws"          % "aws-java-sdk-ses"      % awsVersion,
        "com.amazonaws"          % "aws-java-sdk-dynamodb" % awsVersion,
        "com.amazonaws"          % "aws-java-sdk-kinesis"  % awsVersion,
        "com.amazonaws"          % "aws-java-sdk-s3"       % awsVersion,
        "com.amazonaws"          % "aws-java-sdk-sts"      % awsVersion,
        "software.amazon.awssdk" % "regions"               % "2.5.69",
        "com.github.seratch"     %% "awscala-s3"           % "0.8.2"
      )

      //FIXME : log4j に依存してて以下が出るためにコメントアウト
      //[error] log4j:WARN No appenders could be found for logger (com.amazonaws.AmazonWebServiceClient).
      //      lazy val mailDeps = Seq(
      //        "eu.medsea.mimeutil" % "mime-util" % "2.1.3" exclude ("org.slf4j", "slf4j-log4j12")
      //      )

      val shttpVer = "1.6.7"
      lazy val httpDeps = Seq(
        "com.softwaremill.sttp" %% "core"                            % shttpVer,
        "com.softwaremill.sttp" %% "circe"                           % shttpVer,
        "com.softwaremill.sttp" %% "async-http-client-backend-monix" % shttpVer
      )

      lazy val excelDeps = Seq(
        "info.folone" %% "poi-scala" % "0.19"
      )

      lazy val redisDeps = Seq(
        "com.github.etaty" %% "rediscala" % "1.8.0"
      )

      lazy val scalaPBDeps = Seq(
//        "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
      )

    }

    object LibPj {
      lazy val Deps =
        circeDeps ++
          utilsDeps ++
          loggingDeps ++
          monixDeps ++
          effDeps ++
          testDeps ++
          diDeps

      val monixVer = "3.0.0"
      lazy val monixDeps = Seq(
        "io.monix" %% "monix"      % monixVer,
        "io.monix" %% "monix-eval" % monixVer
        //        "io.monix" %% "monix-cats" % monixVer
      )

      val effVer = "5.3.0"
      lazy val effDeps = Seq(
        "org.atnos" %% "eff"       % effVer,
        "org.atnos" %% "eff-monix" % effVer
      )

      lazy val utilsDeps = Seq(
        "org.typelevel"                 %% "cats-core"     % "2.0.0",
        "com.codecommit"                %% "shims"         % "1.7.0",
        "com.github.scopt"              %% "scopt"         % "3.7.1",
        "com.eaio.uuid"                 % "uuid"           % "3.2",
        "com.github.nscala-time"        %% "nscala-time"   % "2.22.0",
        "org.codehaus.janino"           % "janino"         % "3.0.15",
        "com.jsuereth"                  %% "scala-arm"     % "2.0",
        "com.iheart"                    %% "ficus"         % "1.4.7",
        "joda-time"                     % "joda-time"      % "2.10.3",
        "com.google.crypto.tink"        % "tink"           % "1.3.0-rc1" exclude ("com.google.guava", "guava-jdk5"),
        "com.google.crypto.tink"        % "tink-awskms"    % "1.3.0-rc1",
        "com.googlecode.libphonenumber" % "libphonenumber" % "8.10.22",
        "com.github.tototoshi"          %% "scala-csv"     % "1.3.6",
        "jp.t2v"                        %% "holidays"      % "6.0"
      )

      lazy val diDeps = Seq(
        "com.google.inject"            % "guice"                % "4.2.2",
        "com.google.inject.extensions" % "guice-assistedinject" % "4.2.2"
      )

      lazy val loggingDeps = Seq(
        "com.typesafe.scala-logging" %% "scala-logging"   % "3.9.2",
        "ch.qos.logback"             % "logback-classic"  % "1.2.3",
        "org.slf4j"                  % "slf4j-api"        % "1.7.28",
        "io.sentry"                  % "sentry-logback"   % "1.7.27",
        "com.github.nomadblacky"     % "sentry-config"    % "0.4.0",
        "org.slf4j"                  % "log4j-over-slf4j" % "1.7.28"
      )

      val circeVer = "0.12.1"
      lazy val circeDeps = Seq(
        "org.json4s"             %% "json4s-native"        % "3.2.11", // gmoまわりをcirceにうつして消す
        "io.circe"               %% "circe-core"           % circeVer,
        "io.circe"               %% "circe-generic"        % circeVer,
        "io.circe"               %% "circe-generic-extras" % circeVer,
        "io.circe"               %% "circe-parser"         % circeVer,
        "com.pauldijou"          %% "jwt-circe"            % "4.1.0",
        "io.github.scalapb-json" %% "scalapb-circe"        % "0.5.0"
      )

      val circeForSparkVer = "0.11.1"
      lazy val circeForSparkDeps = Seq(
        "org.json4s"             %% "json4s-native"        % "3.2.11", // gmoまわりをcirceにうつして消す
        "io.circe"               %% "circe-core"           % circeForSparkVer,
        "io.circe"               %% "circe-generic"        % circeForSparkVer,
        "io.circe"               %% "circe-generic-extras" % circeForSparkVer,
        "io.circe"               %% "circe-parser"         % circeForSparkVer,
        "io.github.scalapb-json" %% "scalapb-circe"        % "0.5.0"
      )

      lazy val testDeps = Seq(
        "org.scalatest"       %% "scalatest"             % "3.0.8"  % Test,
        "org.mockito"         %% "mockito-scala"         % "1.5.16" % Test,
        "com.danielasfregola" %% "random-data-generator" % "2.7"    % Test
      )

    }

    object DomainPj {
      lazy val Deps = Seq()
    }

    object BatchPj {
      lazy val Deps = Seq()
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
