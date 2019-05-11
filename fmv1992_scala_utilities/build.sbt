// https://www.scala-sbt.org/1.0/docs/Howto-Project-Metadata.html
import xerial.sbt.Sonatype._

lazy val topLevelSettings = Seq(
coverageMinimum := 90,
coverageFailOnMinimum := true,


// Publishing information. --- {{{
// See: https://github.com/xerial/sbt-sonatype


usePgpKeyHex("1FEB8CD8FBFDC1CB"),

// --- }}}

version := IO.readLines(new File("./src/main/resources/version")).mkString(""),


publishConfiguration := publishConfiguration.value.withOverwrite(true),
publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
)

lazy val commonSettings = topLevelSettings ++ Seq(
    // name := "fmv1992_scala_utilities",
    organization := "fmv1992",
    version := IO.readLines(new File("./src/main/resources/version")).mkString(""),
    scalaVersion := "2.12.8",
    // Your profile name of the sonatype account. The default is the same with the organization value
    sonatypeProfileName := "io.github.fmv1992",
    // To sync with Maven central, you need to supply the following information:
    publishMavenStyle := true,
    licenses := Seq("GPLv2" -> url("https://www.gnu.org/licenses/gpl-2.0.html")),
    // Where is the source code hosted
    sonatypeProjectHosting := Some(GitHubHosting("fmv1992", "fmv1992_scala_utilities", "fmv1992@gmail.com")),
    pollInterval := scala.concurrent.duration.FiniteDuration(150L, "ms"),

    publishTo := sonatypePublishTo.value,
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true),

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

    scalacOptions ++= (
      Seq(
        "-feature",
        "-deprecation",
        "-Xfatal-warnings")
      ++ sys.env.get("SCALAC_OPTS").getOrElse("").split(" ").toSeq)
      )

lazy val GOLSettings = Seq(assemblyJarName in assembly := "game_of_life.jar")

lazy val uniqSettings = Seq(assemblyJarName in assembly := "uniq.jar")

lazy val fmv1992_scala_utilitiesSettings = Seq(assemblyJarName in assembly := "root.jar")

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
lazy val util       = (project in file("./src/main/scala/fmv1992/fmv1992_scala_utilities/util")).settings(commonSettings)

lazy val gameOfLife = (project in file("./src/main/scala/fmv1992/fmv1992_scala_utilities/game_of_life")).settings(commonSettings).settings(GOLSettings).dependsOn(util).dependsOn(cli)

lazy val uniq       = (project in file("./src/main/scala/fmv1992/fmv1992_scala_utilities/uniq")).settings(commonSettings).settings(uniqSettings).dependsOn(util).dependsOn(cli)

lazy val cli        = (project in file("./src/main/scala/fmv1992/fmv1992_scala_utilities/cli")).settings(commonSettings).dependsOn(util)

// Root project.
lazy val fmv1992_scala_utilities = (project in file("."))
  .settings(fmv1992_scala_utilitiesSettings)
  .settings(commonSettings)
  .aggregate(
    cli,
    gameOfLife,
    uniq,
    util
    )
  // .settings(publishArtifact := false,  // ???: Remove this section on next publishing?
  //           publishTo := None,
  //           skip in publish := true)
