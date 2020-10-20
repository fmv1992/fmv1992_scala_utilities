// https://www.scala-sbt.org/1.0/docs/Howto-Project-Metadata.html

// References on how to do multi project builds:
// 1.   https://www.scala-sbt.org/1.x/docs/Cross-Build.html
//      *   Used by this repos.
// 2.   https://github.com/sbt/sbt-projectmatrix
//      *   For some reason this messed up with `sbt-assembly`.

lazy val scala212 = "2.12.8"
lazy val scala211 = "2.11.12"
lazy val supportedScalaVersions = List(scala211)

resolvers += Resolver.mavenLocal

coverageMinimum := 90
coverageFailOnMinimum := true

name := "fmv1992_scala_utilities"
homepage := Some(url("https:???"))

ThisBuild / scalaVersion := scala211
ThisBuild / crossScalaVersions := supportedScalaVersions
ThisBuild / mainClass in Compile := Some(
  "fmv1992.fmv1992_scala_utilities.uniq.Uniq"
)

lazy val nativeSettings = Seq(
  nativeLinkStubs := true,
  nativeLinkStubs in runMain := true,
  Test / nativeLinkStubs := true,
  // ???: [error] (Compile / doc) Scaladoc generation failed
  sources in (Compile, doc) := Seq.empty
)

enablePlugins(ScalaNativePlugin)

test in assembly := {}
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.rename
  // case PathList(ps @ _*) if ps.contains("scalanative") => MergeStrategy.first
  // case x if x.contains("org.scala-native") => MergeStrategy.first
  // case PathList(ps @ _*) if ps.last.endsWith(".nir") => MergeStrategy.keep
  // case PathList(ps @ _*)                               => scala.Console.err.println("‡" + ps.toString) ; MergeStrategy.first
  // case PathList(ps @ _*)                               => scala.Console.err.println("‡" + ps.toString) ; MergeStrategy.singleOrError
  case x => MergeStrategy.first
  // case x => {
  //   val oldStrategy = (assemblyMergeStrategy in assembly).value
  //   oldStrategy(x)
  // }
}

// ???: Try to remove all resource files from the assembly process to check if
// the macro is working.
//
// (unmanagedResourceDirectories in Compile) := (unmanagedResourceDirectories in Compile).value .filter(x => false)
// (unmanagedResourceDirectories in assembly) := (unmanagedResourceDirectories in assembly).value .filter(x => false)
//
// mappings in (Compile, packageBin) ~= {
//   x =>
//     {
//       scala.Console.err.println(x)
//       x.filter(y => {
//         scala.Console.err.println(y)
//         !y._1.getName.endsWith(".conf")
//       })
//       throw new Exception()
//     }
// }

lazy val commonSettings = Seq(
  organization := "fmv1992",
  licenses += "GPLv2" -> url("https://www.gnu.org/licenses/gpl-2.0.html"),
  version := IO
    .readLines(new File("./src/main/resources/version"))
    .mkString(""),
  scalaVersion := scala211,
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
  libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.0" % Test,
  // https://stackoverflow.com/questions/20490108/what-happened-to-the-macros-api-in-scala-2-11
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scala211,
  libraryDependencies += "fmv1992" %%% "scala_cli_parser" % "0.+",
  // testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oU"),
  // parallelExecution := false,

  // logLevel in assembly := Level.Debug,
  scalacOptions ++= (Seq(
    "-feature",
    "-deprecation",
    "-Xfatal-warnings"
    // "-optmize"
  )
    ++ sys.env.get("SCALAC_OPTS").getOrElse("").split(" ").toSeq)
)

lazy val GOLSettings = Seq(assemblyJarName in assembly := "game_of_life.jar")

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
lazy val util =
  (project in file("./src/main/scala/fmv1992/fmv1992_scala_utilities/util"))
    .settings(commonSettings)
    .settings(crossScalaVersions := supportedScalaVersions)
    .settings(nativeSettings)
    .enablePlugins(ScalaNativePlugin)

lazy val gameOfLife = (project in file(
  "./src/main/scala/fmv1992/fmv1992_scala_utilities/game_of_life"
)).settings(commonSettings)
  .settings(GOLSettings)
  .settings(crossScalaVersions := supportedScalaVersions)
  .settings(nativeSettings)
  .dependsOn(util, cli)
  .enablePlugins(ScalaNativePlugin)

lazy val uniq =
  (project in file("./src/main/scala/fmv1992/fmv1992_scala_utilities/uniq"))
    .settings(commonSettings)
    .settings(uniqSettings)
    .settings(crossScalaVersions := supportedScalaVersions)
    .settings(nativeSettings)
    .dependsOn(util, cli)
    .enablePlugins(ScalaNativePlugin)

lazy val cli = (project in file(
  "./src/main/scala/fmv1992/fmv1992_scala_utilities/cli"
)).settings(commonSettings)
  .settings(crossScalaVersions := supportedScalaVersions)
  .settings(nativeSettings)
  .dependsOn(util)
  .enablePlugins(ScalaNativePlugin)

// Root project.
lazy val fmv1992_scala_utilities = (project in file("."))
  .settings(fmv1992_scala_utilitiesSettings)
  .settings(commonSettings)
  .settings(crossScalaVersions := supportedScalaVersions)
  .settings(nativeSettings)
  .dependsOn(util)
  .aggregate(
    cli,
    gameOfLife,
    uniq,
    util
  )
  .enablePlugins(ScalaNativePlugin)
