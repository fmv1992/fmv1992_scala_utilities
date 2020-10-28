// https://www.scala-sbt.org/1.0/docs/Howto-Project-Metadata.html
import xerial.sbt.Sonatype._

// References on how to do multi project builds:
// 1.   https://www.scala-sbt.org/1.x/docs/Cross-Build.html
//      *   Used by this repos.
// 2.   https://github.com/sbt/sbt-projectmatrix
//      *   For some reason this messed up with `sbt-assembly`.

ThisBuild / scalaVersion := scala213
scalaVersion := scala213

// https://github.com/SemanticSugar/sconfig/blob/9623f8401321fe847a49aecb7cfd92be73872ff6/build.sbt#L52
lazy val scala211 = "2.11.12"
lazy val scala212 = "2.12.12"
lazy val scala213 = "2.13.3"

val versionsBase = Seq(scala211, scala212, scala213)
val versionsJVM = versionsBase
val versionsNative = Seq(scala211)

// coverageMinimum := 90
// coverageFailOnMinimum := true

// enablePlugins(ScalaNativePlugin)
// nativeLinkStubs := true
// nativeLinkStubs in runMain := true
// Test / nativeLinkStubs := true
// // ???: [error] (Compile / doc) Scaladoc generation failed
// sources in (Compile, doc) := Seq.empty

inThisBuild(
  List(
    libraryDependencies += "org.scalameta" %% "scalameta" % "4.3.24",
    semanticdbEnabled := true,
    semanticdbOptions += "-P:semanticdb:synthetics:on",
    semanticdbVersion := scalafixSemanticdb.revision,
    scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(
      scalaVersion.value
    ),
    addCompilerPlugin(
      "org.scalameta" % "semanticdb-scalac" % "4.3.24" cross CrossVersion.full
    ),
    addCompilerPlugin(scalafixSemanticdb)
  )
)

lazy val commonSettings = Seq(
  homepage := Some(url("https://github.com/fmv1992/fmv1992_scala_utilities")),
  organization := "io.github.fmv1992",
  licenses += "GPLv2" -> url("https://www.gnu.org/licenses/gpl-2.0.html"),
  version := IO
    .readLines(new File("./src/main/resources/version"))
    .mkString(""),
  // crossScalaVersions := supportedScalaVersions,
  //
  pollInterval := scala.concurrent.duration.FiniteDuration(150L, "ms"),
  // Workaround according to: https://github.com/sbt/sbt/issues/3497
  // watchService := (() => new sbt.io.PollingWatchService(pollInterval.value)),
  maxErrors := 100,
  // Ship resource files with each jar.
  resourceDirectory in Compile := file(".") / "./src/main/resources",
  resourceDirectory in Runtime := file(".") / "./src/main/resources",
  //
  addCompilerPlugin(scalafixSemanticdb),
  scalacOptions ++= (
    Seq(
      "-P:semanticdb:synthetics:on",
      "-Yrangepos",
      "-Ywarn-dead-code",
      "-deprecation",
      "-feature"
      // "-Xfatal-warnings",
      // "-Ywarn-unuse"
    )
      ++ sys.env.get("SCALAC_OPTS").getOrElse("").split(" ").toSeq
  ),
  //
  // logLevel in assembly := Level.Debug,
  //
  test in assembly := {},
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.rename
    case x                                   => MergeStrategy.first
  },
  sonatypeProfileName := "io.github.fmv1992",
  publishMavenStyle := true,
  sonatypeProjectHosting := Some(
    GitHubHosting("fmv1992", "fmv1992_scala_utilities", "fmv1992@gmail.com")
  ),
  licenses := Seq("GPLv2" -> url("https://www.gnu.org/licenses/gpl-2.0.html")),
  organization := "io.github.fmv1992",
  // or if you want to set these fields manually
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/fmv1992/fmv1992_scala_utilities"),
      "scm:git@github.com:fmv1992/fmv1992_scala_utilities.git"
    )
  ),
  developers := List(
    Developer(
      id = "fmv1992",
      name = "Felipe Martins Vieira",
      email = "fmv1992@gmail.com",
      url = url("https://github.com/fmv1992/")
    )
  ),
  publishConfiguration := publishConfiguration.value.withOverwrite(true),
  publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(
    true
  ),
  publishTo in ThisBuild := sonatypePublishTo.value,
  //
  target := {
    (ThisBuild / baseDirectory).value / "target" / thisProject.value.id
  }
)

lazy val scalaNativeSettings = Seq(
  crossScalaVersions := List(scala211),
  scalaVersion := scala211, // allows to compile if scalaVersion set not 2.11
  nativeLinkStubs := true
  // Test / test := {}
)

lazy val commonDependencies = Seq(
  //
  libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.0" % Test,
  libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.2.0",
  // https://stackoverflow.com/questions/20490108/what-happened-to-the-macros-api-in-scala-2-11
  // scalafixDependencies += "org.scala-lang.modules" %% "scala-collection-migrations" % "2.2.0",
  // scalafixDependencies in ThisBuild += "org.scala-lang.modules" %% "scala-collection-migrations" % "2.2.0",
  //
  // Scala rewrites.
  // Scala rewrites: https://index.scala-lang.org/scala/scala-rewrites/scala-rewrites/0.1.2?target=_2.13.
  //
  addCompilerPlugin(scalafixSemanticdb),
  //
  scalafixDependencies ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n == 11 => List()
      case Some((2, n)) if n == 12 =>
        List(
        )
      case Some((2, n)) if n == 13 =>
        List(
          "org.scala-lang" %% "scala-rewrites" % "0.1.2"
        )
      case _ => Nil
    }
  },
  libraryDependencies ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n == 11 =>
        List(
          "org.scala-lang" % "scala-reflect" % scala211
        )
      case Some((2, n)) if n == 12 =>
        List("com.sandinh" %% "scala-rewrites" % "0.1.10-sd")
      case Some((2, n)) if n == 13 =>
        List("com.sandinh" %% "scala-rewrites" % "0.1.10-sd")
      case _ => Nil
    }
  },
  Compile / scalacOptions ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n == 11 => List()
      case Some((2, n)) if n == 12 => List("-Xlint:unused")
      case Some((2, n)) if n == 13 => List("-Xlint:unused")
    }
  }
  //
)

lazy val crossProjectConfig =
  crossProject(JVMPlatform, NativePlatform)
    .crossType(CrossType.Full)
    .jvmSettings(
      crossScalaVersions := versionsJVM
    )
    .nativeSettings(
      scalaNativeSettings
      // crossScalaVersions := versionsNative,
      // scalaVersion := scala211, // allows to compile if scalaVersion set not 2.11
      // sharedJvmNativeSource,
      // nativeLinkStubs := true,
      // logLevel := Level.Info, // Info or Debug
      // libraryDependencies += "com.github.lolgab" %%% "minitest" % "2.5.0-5f3852e" % Test,
      // testFrameworks += new TestFramework("minitest.runner.Framework")
    )

lazy val commonSettingsAndDependencies = commonSettings ++ commonDependencies

lazy val GOLSettings = Seq(
  assemblyJarName in assembly := "game_of_life.jar",
  mainClass in Compile := Some(
    "fmv1992.fmv1992_scala_utilities.game_of_life.GameOfLife"
  )
)

lazy val uniqSettings = Seq(
  assemblyJarName in assembly := "uniq.jar",
  mainClass in Compile := Some("fmv1992.fmv1992_scala_utilities.uniq.Uniq")
)

lazy val fmv1992_scala_utilitiesSettings = Seq(
  assemblyJarName in assembly := "root.jar",
  mainClass in Compile := Some("fmv1992.fmv1992_scala_utilities.uniq.Uniq")
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
lazy val utilNative = crossProjectConfig.native
  .in(file("./src/main/scala/fmv1992/fmv1992_scala_utilities/util"))
  .settings(commonSettingsAndDependencies)
  .settings(name := "utilNative")
lazy val utilJVM = crossProjectConfig.jvm
  .in(file("./src/main/scala/fmv1992/fmv1992_scala_utilities/util"))
  .settings(crossScalaVersions := versionsJVM)

// lazy val gameOfLife =
//   crossProject
//     .crossType(CrossType.Pure)
//     .crossProject(JVMPlatform, NativePlatform)
//     .nativeSettings(scalaNativeSettings)
//     .in(file("./src/main/scala/fmv1992/fmv1992_scala_utilities/game_of_life"))
//     .settings(commonSettingsAndDependencies)
//     .settings(GOLSettings)
//     .settings(crossScalaVersions := supportedScalaVersions)
//     .dependsOn(util, cli)
//
// lazy val uniq =
//   crossProject
//     .crossType(CrossType.Pure)
//     .crossProject(JVMPlatform, NativePlatform)
//     .nativeSettings(scalaNativeSettings)
//     .in(file("./src/main/scala/fmv1992/fmv1992_scala_utilities/uniq"))
//     .settings(commonSettingsAndDependencies)
//     .settings(uniqSettings)
//     .settings(crossScalaVersions := supportedScalaVersions)
//     .dependsOn(util, cli)
//
// lazy val cli =
//   crossProject
//     .crossType(CrossType.Pure)
//     .crossProject(JVMPlatform, NativePlatform)
//     .nativeSettings(scalaNativeSettings)
//     .in(file("./src/main/scala/fmv1992/fmv1992_scala_utilities/cli"))
//     .settings(commonSettingsAndDependencies)
//     .settings(crossScalaVersions := supportedScalaVersions)
//     .dependsOn(util)

// Root project.
// lazy val root =
lazy val fmv1992_scala_utilities =
  (project in file("."))
    .settings(fmv1992_scala_utilitiesSettings)
    .settings(commonSettingsAndDependencies)
    .settings(
      publish / skip := true,
      crossScalaVersions := Nil,
      doc / aggregate := false,
      packageDoc / aggregate := false
    )
    .dependsOn(utilJVM)
    .aggregate(
      // cli,
      // gameOfLife,
      // uniq,
      utilJVM,
      utilNative
    )
