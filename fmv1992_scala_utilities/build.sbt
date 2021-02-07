// https://www.scala-sbt.org/1.0/docs/Howto-Project-Metadata.html
import xerial.sbt.Sonatype._

Global / onChangedBuildSource := ReloadOnSourceChanges

// https://github.com/SemanticSugar/sconfig/blob/9623f8401321fe847a49aecb7cfd92be73872ff6/build.sbt#L52
lazy val scala213 = "2.13.4"

// val versionsJVM = Seq(scala211, scala212, scala213)
val versionsJVM = Seq(scala213)
val versionsNative = Seq(scala213)

inThisBuild(
  List(
    scalaVersion := scala213,
    scalafixScalaBinaryVersion :=
      CrossVersion.binaryScalaVersion(scalaVersion.value),
  ),
)

// coverageMinimum := 90
// coverageFailOnMinimum := true

lazy val commonSettings = Seq(
  // General.
  homepage := Some(url("https://github.com/fmv1992/fmv1992_scala_utilities")),
  organization := "io.github.fmv1992",
  licenses += "GPLv2" -> url("https://www.gnu.org/licenses/gpl-2.0.html"),
  version := IO
    .readLines(new File("./util/shared/src/main/resources/version"))
    .mkString(""),
  pollInterval := scala.concurrent.duration.FiniteDuration(150L, "ms"),
  // Ship resource files with each jar.
  resourceDirectory in Compile := file(".") / "./src/main/resources",
  resourceDirectory in Runtime := file(".") / "./src/main/resources",
  //
  scalacOptions ++= (
    Seq(
      "-deprecation",
      "-feature",
      "-P:semanticdb:synthetics:on",
      "-Xlint:unused",
      "-Yrangepos",
      "-Ywarn-dead-code",
    )
      ++ sys.env.get("SCALAC_OPTS").getOrElse("").split(" ").toSeq
  ),
  // Assembly.
  // logLevel in assembly := Level.Debug,
  //
  test in assembly := {},
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.rename
    case x                                   => MergeStrategy.first
  },
  // Scalafix.
  // <https://scalacenter.github.io/scalafix/docs/users/installation.html>.
  libraryDependencies += "org.scalameta" %% "scalameta" % "4.3.24",
  semanticdbEnabled := true,
  semanticdbOptions += "-P:semanticdb:synthetics:on",
  semanticdbVersion := scalafixSemanticdb.revision,
  scalafixScalaBinaryVersion := CrossVersion.binaryScalaVersion(
    scalaVersion.value,
  ),
  addCompilerPlugin(
    "org.scalameta" % "semanticdb-scalac" % "4.3.24" cross CrossVersion.full,
  ),
  addCompilerPlugin(scalafixSemanticdb),
  //
  // Scala rewrites <https://index.scala-lang.org/scala/scala-rewrites/scala-rewrites/0.1.2?target=_2.13>: See below.
  //
  // Publishing.
  sonatypeProfileName := "io.github.fmv1992",
  publishMavenStyle := true,
  sonatypeProjectHosting := Some(
    GitHubHosting("fmv1992", "fmv1992_scala_utilities", "fmv1992@gmail.com"),
  ),
  licenses := Seq("GPLv2" -> url("https://www.gnu.org/licenses/gpl-2.0.html")),
  // or if you want to set these fields manually
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/fmv1992/fmv1992_scala_utilities"),
      "scm:git@github.com:fmv1992/fmv1992_scala_utilities.git",
    ),
  ),
  developers := List(
    Developer(
      id = "fmv1992",
      name = "Felipe Martins Vieira",
      email = "fmv1992@gmail.com",
      url = url("https://github.com/fmv1992/"),
    ),
  ),
  publishConfiguration := publishConfiguration.value.withOverwrite(true),
  publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(
    true,
  ),
  publishTo in ThisBuild := sonatypePublishTo.value,
  credentials += Credentials(
    file(
      sys.env
        .get("SBT_CREDENTIALS_PATH")
        .getOrElse(""),
    ),
  ),
  usePgpKeyHex(
    sys.env
      .get("SBT_PGP_KEY")
      .getOrElse("B145230D09E5330C9A0ED5BC1FEB8CD8FBFDC1CB"),
  ),
  //
  target := {
    (ThisBuild / baseDirectory).value / "target" / thisProject.value.id
  },
)

lazy val scalaNativeSettings = Seq(
  crossScalaVersions := versionsNative,
  nativeLinkStubs := true,
  nativeLinkStubs in runMain := true,
  nativeLinkStubs in Test := true,
  Test / nativeLinkStubs := true,
  sources in (Compile, doc) := Seq.empty,
)

lazy val commonDependencies = Seq(
  //
  libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.4-M1" % Test,
  libraryDependencies += "org.scala-lang.modules" %%% "scala-collection-compat" % "2.4.0",
  // Scala rewrites: https://index.scala-lang.org/scala/scala-rewrites/scala-rewrites/0.1.2?target=_2.13.
  libraryDependencies += "com.sandinh" %% "scala-rewrites" % "0.1.10-sd",
  //
  scalafixDependencies += "org.scala-lang" %% "scala-rewrites" % "0.1.2",
  Compile / scalacOptions += "",
)

lazy val commonSettingsAndDependencies = commonSettings ++ commonDependencies

lazy val fmv1992_scala_utilitiesSettings = Seq(
  assemblyJarName in assembly := "root.jar",
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
    .crossType(CrossType.Full)
    .settings(commonSettingsAndDependencies)
    .jvmSettings(
      crossScalaVersions := versionsJVM,
    )
    .nativeSettings(
      scalaNativeSettings,
    )
lazy val utilJVM: sbt.Project = util.jvm
lazy val utilNative: sbt.Project = util.native

lazy val root: sbt.Project =
  (project in file("."))
    .settings(fmv1992_scala_utilitiesSettings)
    .settings(commonSettingsAndDependencies)
    .settings(
      publish / skip := true,
      test / skip := true,
      doc / aggregate := false,
      crossScalaVersions := Nil,
      packageDoc / aggregate := false,
    )
    .dependsOn(utilJVM)
    .aggregate(
      utilJVM,
      utilNative,
    )

// --- }}}
