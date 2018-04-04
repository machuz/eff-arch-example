name := "eff-arch-sample"
version := "1.0.0"

scalaVersion := "2.12.4"
scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-Ypartial-unification",
  "-language:experimental.macros",
  "-language:implicitConversions"
)

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

val shttpVer = "1.1.4"
val circeVer = "0.8.0"
val effVer = "4.5.0"

libraryDependencies ++= Seq(
  "com.github.pureconfig"      %% "pureconfig"                      % "0.7.2",
  "com.softwaremill.quicklens" %% "quicklens"                       % "1.4.11",
  "com.typesafe.akka"          %% "akka-actor"                      % "2.4.19",
  "com.typesafe.akka"          %% "akka-http"                       % "10.0.10",
  "de.heikoseeberger"          %% "akka-http-circe"                 % "1.18.1",
  "io.circe"                   %% "circe-core"                      % circeVer,
  "io.circe"                   %% "circe-generic"                   % circeVer,
  "io.circe"                   %% "circe-generic-extras"            % circeVer,
  "io.circe"                   %% "circe-java8"                     % circeVer,
  "io.circe"                   %% "circe-jawn"                      % circeVer,
  "org.atnos"                  %% "eff"                             % effVer,
  "org.atnos"                  %% "eff-monix"                       % effVer,
  "org.typelevel"              %% "cats-core"                       % "0.9.0",
  "org.zalando"                %% "grafter"                         % "2.3.0",
  "ch.qos.logback"             % "logback-classic"                  % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging"                   % "3.7.2",
  "com.softwaremill.sttp"      %% "core"                            % shttpVer,
  "com.softwaremill.sttp"      %% "async-http-client-backend-monix" % shttpVer,
  "com.softwaremill.sttp"      %% "circe"                           % shttpVer,
  compilerPlugin("org.spire-math"  %% "kind-projector" % "0.9.4"),
  compilerPlugin("org.scalamacros" %% "paradise"       % "2.1.1" cross CrossVersion.full)
)
