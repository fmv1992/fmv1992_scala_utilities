// https://www.scala-sbt.org/1.0/docs/Howto-Project-Metadata.html
import xerial.sbt.Sonatype._

// References on how to do multi project builds:
// 1.   https://www.scala-sbt.org/1.x/docs/Cross-Build.html
//      *   Used by this repos.
// 2.   https://github.com/sbt/sbt-projectmatrix
//      *   For some reason this messed up with `sbt-assembly`.

// ThisBuild / scalaVersion := scala213
// scalaVersion := scala213

// https://github.com/SemanticSugar/sconfig/blob/9623f8401321fe847a49aecb7cfd92be73872ff6/build.sbt#L52
lazy val scala211 = "2.11.12"
lazy val scala212 = "2.12.12"
lazy val scala213 = "2.13.3"

// val versionsJVM = Seq(scala211, scala212, scala213)
val versionsJVM = Seq(scala211, scala212, scala213)
val versionsNative = Seq(scala211)

// coverageMinimum := 90
// coverageFailOnMinimum := true

inThisBuild(
  List(
    resolvers += Resolver.mavenLocal,
    //
    // parallelExecution in ThisBuild := false,
    // concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    //
    //
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
    .readLines(new File("./util/src/main/resources/version"))
    .mkString(""),
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
  credentials += Credentials(
    file(
      sys.env
        .get("SBT_CREDENTIALS_PATH")
        .getOrElse("")
    )
  ),
  usePgpKeyHex(
    sys.env
      .get("SBT_PGP_KEY")
      .getOrElse("B145230D09E5330C9A0ED5BC1FEB8CD8FBFDC1CB")
  ),
  //
  target := {
    (ThisBuild / baseDirectory).value / "target" / thisProject.value.id
  }
)

lazy val scalaNativeSettings = Seq(
  crossScalaVersions := versionsNative,
  scalaVersion := scala211, // allows to compile if scalaVersion set not 2.11
  nativeLinkStubs := true,
  nativeLinkStubs in runMain := true,
  nativeLinkStubs in Test := true,
  Test / nativeLinkStubs := true,
  sources in (Compile, doc) := Seq.empty
)

lazy val commonDependencies = Seq(
  //
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.4-M1" % Test,
  libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.4.1",
  // libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.2.0+20-b3aadcba-SNAPSHOT",
  // https://stackoverflow.com/questions/20490108/what-happened-to-the-macros-api-in-scala-2-11
  // libraryDependencies += "org.scala-lang" % "scala-reflect" % scala211,
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
          "org.scalatest" %%% "scalatest" % "3.2.4-M1" % Test,
          "org.scala-lang.modules" %%% "scala-collection-compat" % "2.4.1"
        )
      case Some((2, n)) if n == 12 =>
        List(
          "com.sandinh" %% "scala-rewrites" % "0.1.10-sd",
          "org.scalatest" %% "scalatest" % "3.2.4-M1" % Test,
          "org.scala-lang.modules" %% "scala-collection-compat" % "2.4.1"
        )
      case Some((2, n)) if n == 13 =>
        List(
          "com.sandinh" %% "scala-rewrites" % "0.1.10-sd",
          "org.scalatest" %% "scalatest" % "3.2.4-M1" % Test,
          "org.scala-lang.modules" %% "scala-collection-compat" % "2.4.1"
        )
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

// Projects definitions. --- {{{

// Unfortunately there's no way to factor out `sbtcrossproject.CrossProject`
// instance creations. See:
// <https://gitter.im/scala-native/sbt-crossproject?at=5f9aa5d906fa0513dd7da676>.
lazy val util: sbtcrossproject.CrossProject =
  crossProject(JVMPlatform, NativePlatform)
    .crossType(CrossType.Pure)
    .settings(commonSettingsAndDependencies)
    .jvmSettings(
      crossScalaVersions := versionsJVM
    )
    .nativeSettings(
      scalaNativeSettings
    )
lazy val utilJVM: sbt.Project = util.jvm
  .in(file("./util"))
lazy val utilNative: sbt.Project = util.native
  .in(file("./util"))
  .settings(
    nativeLink := file("")
  )

lazy val gameOfLife: sbtcrossproject.CrossProject =
  crossProject(JVMPlatform, NativePlatform)
    .crossType(CrossType.Pure)
    .settings(commonSettingsAndDependencies)
    .jvmSettings(
      crossScalaVersions := versionsJVM
    )
    .nativeSettings(
      scalaNativeSettings
    )
    .dependsOn(cli)
    .dependsOn(util)
lazy val gameOfLifeJVM: sbt.Project = gameOfLife.jvm
  .in(file("game_of_life"))
  .settings(GOLSettings)
  .dependsOn(utilJVM)
  .dependsOn(cliJVM)
lazy val gameOfLifeNative: sbt.Project = gameOfLife.native
  .in(file("game_of_life"))
  .dependsOn(utilNative)
  .dependsOn(cliNative)

lazy val uniq: sbtcrossproject.CrossProject =
  crossProject(JVMPlatform, NativePlatform)
    .crossType(CrossType.Pure)
    .settings(commonSettingsAndDependencies)
    .jvmSettings(
      crossScalaVersions := versionsJVM
    )
    .nativeSettings(
      scalaNativeSettings
    )
    .dependsOn(cli)
    .dependsOn(util)
lazy val uniqJVM: sbt.Project = uniq.jvm
  .in(file("uniq"))
  .settings(uniqSettings)
  .dependsOn(utilJVM)
  .dependsOn(cliJVM)
lazy val uniqNative: sbt.Project = uniq.native
  .in(file("uniq"))
  .dependsOn(utilNative)
  .dependsOn(cliNative)

lazy val cli: sbtcrossproject.CrossProject =
  crossProject(JVMPlatform, NativePlatform)
    .crossType(CrossType.Pure)
    .settings(commonSettingsAndDependencies)
    .jvmSettings(
      crossScalaVersions := versionsJVM
    )
    .nativeSettings(
      scalaNativeSettings
    )
    .dependsOn(util % "compile->compile;test->test")
lazy val cliJVM = cli.jvm
  .in(file("cli"))
  .dependsOn(utilJVM)
lazy val cliNative: sbt.Project = cli.native
  .in(file("cli"))
  .dependsOn(utilNative)
  .settings(
    nativeLink := file("")
  )

lazy val root: sbt.Project =
  (project in file("."))
    .settings(fmv1992_scala_utilitiesSettings)
    .settings(commonSettingsAndDependencies)
    .settings(
      publish / skip := true,
      doc / aggregate := false,
      crossScalaVersions := Nil,
      packageDoc / aggregate := false
    )
    .dependsOn(utilJVM)
    // .dependsOn(utilNative)
    .aggregate(
      utilJVM,
      utilNative,
      gameOfLifeJVM,
      gameOfLifeNative,
      uniqJVM,
      uniqNative,
      cliJVM,
      cliNative
    )

// --- }}}
