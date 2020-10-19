// https://www.scala-sbt.org/1.0/docs/Howto-Project-Metadata.html
import xerial.sbt.Sonatype._

// References on how to do multi project builds:
// 1.   https://www.scala-sbt.org/1.x/docs/Cross-Build.html
//      *   Used by this repos.
// 2.   https://github.com/sbt/sbt-projectmatrix
//      *   For some reason this messed up with `sbt-assembly`.

lazy val scala212 = "2.12.8"
lazy val scala211 = "2.11.12"
lazy val supportedScalaVersions = List(scala212, scala211)
ThisBuild / scalaVersion := scala212
ThisBuild / crossScalaVersions := supportedScalaVersions

coverageMinimum := 90
coverageFailOnMinimum := true

name := "fmv1992_scala_utilities"

publishTo in ThisBuild := sonatypePublishTo.value
sonatypeProfileName := "io.github.fmv1992"
publishMavenStyle := true
sonatypeProjectHosting := Some(
  GitHubHosting("fmv1992", "fmv1992_scala_utilities", "fmv1992@gmail.com")
)
licenses := Seq("GPLv2" -> url("https://www.gnu.org/licenses/gpl-2.0.html"))
organization := "io.github.fmv1992"

// or if you want to set these fields manually
homepage := Some(url("https://github.com/fmv1992/fmv1992_scala_utilities"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/fmv1992/fmv1992_scala_utilities"),
    "scm:git@github.com:fmv1992/fmv1992_scala_utilities.git"
  )
)
developers := List(
  Developer(
    id = "fmv1992",
    name = "Felipe Martins Vieira",
    email = "fmv1992@gmail.com",
    url = url("https://github.com/fmv1992/")
  )
)

publishConfiguration := publishConfiguration.value.withOverwrite(true)
publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)

lazy val commonSettings = Seq(
  publishConfiguration := publishConfiguration.value.withOverwrite(true),
  publishLocalConfiguration := publishLocalConfiguration.value
    .withOverwrite(true),
  sonatypeProfileName := "io.github.fmv1992",
  publishMavenStyle := true,
  sonatypeProjectHosting := Some(
    GitHubHosting("fmv1992", "fmv1992_scala_utilities", "fmv1992@gmail.com")
  ),
  licenses := Seq("GPLv2" -> url("https://www.gnu.org/licenses/gpl-2.0.html")),
  organization := "io.github.fmv1992",
  version := IO
    .readLines(new File("./src/main/resources/version"))
    .mkString(""),
  scalaVersion := "2.12.8",
  pollInterval := scala.concurrent.duration.FiniteDuration(150L, "ms"),
  // Workaround according to: https://github.com/sbt/sbt/issues/3497
  watchService := (() => new sbt.io.PollingWatchService(pollInterval.value)),
  maxErrors := 100,
  // Ship resource files with each jar.
  resourceDirectory in Compile := file(".") / "./src/main/resources",
  resourceDirectory in Runtime := file(".") / "./src/main/resources",
  // This final part makes test artifacts being only importable by the test files
  // libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  //                                                                   ↑↑↑↑↑
  // Removed on commit 'cd9d482' to enable 'trait ScalaInitiativesTest' define
  // 'namedTest'.
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5",
  // testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oU"),
  // parallelExecution := false,

  // logLevel in assembly := Level.Debug,
  scalacOptions ++= (Seq("-feature", "-deprecation", "-Xfatal-warnings")
    ++ sys.env.get("SCALAC_OPTS").getOrElse("").split(" ").toSeq)
)

lazy val GOLSettings = Seq(assemblyJarName in assembly := "game_of_life.jar")

lazy val uniqSettings = Seq(assemblyJarName in assembly := "uniq.jar")

lazy val fmv1992_scala_utilitiesSettings = Seq(
  assemblyJarName in assembly := "root.jar"
)

// IMPORTANT: The name of the variable is important here. It becomes the name
// of the project on ivy (projectnote01).
//
// However the name of "common" in `file("./common")` is not. Scala seems to be
// agnostic to project layout.
//
// Furthermore this project may be published locally with:
//
// ```
// sbt "clean" "set offline := true" "clean" "update" 'publishLocal'
// ```
//
// But it must be build also locally with:
//
// ```
// sbt "clean" "clean" "update" compile
// ```
//
lazy val util =
  (project in file("./src/main/scala/fmv1992/fmv1992_scala_utilities/util"))
    .settings(commonSettings)
    .settings(crossScalaVersions := supportedScalaVersions)

lazy val gameOfLife = (project in file(
  "./src/main/scala/fmv1992/fmv1992_scala_utilities/game_of_life"
)).settings(commonSettings)
  .settings(GOLSettings)
  .settings(crossScalaVersions := supportedScalaVersions)
  .dependsOn(util)
  .dependsOn(cli)

lazy val uniq =
  (project in file("./src/main/scala/fmv1992/fmv1992_scala_utilities/uniq"))
    .settings(commonSettings)
    .settings(uniqSettings)
    .settings(crossScalaVersions := supportedScalaVersions)
    .dependsOn(util)
    .dependsOn(cli)

lazy val cli = (project in file(
  "./src/main/scala/fmv1992/fmv1992_scala_utilities/cli"
)).settings(commonSettings)
  .settings(crossScalaVersions := supportedScalaVersions)
  .dependsOn(util)

// Root project.
lazy val fmv1992_scala_utilities = (project in file("."))
  .settings(fmv1992_scala_utilitiesSettings)
  .settings(commonSettings)
  .settings(crossScalaVersions := supportedScalaVersions)
  .aggregate(
    cli,
    gameOfLife,
    uniq,
    util
  )
