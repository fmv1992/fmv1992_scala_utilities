import xerial.sbt.Sonatype._

lazy val scala211 = "2.11.12"
lazy val scala212 = "2.12.12"
lazy val scala213 = "2.13.3"

val versionsJVM = Seq(scala211, scala212, scala213)
val versionsNative = Seq(scala211)

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
  pollInterval := scala.concurrent.duration.FiniteDuration(150L, "ms"),
  maxErrors := 100,
  resourceDirectory in Compile := file(".") / "./src/main/resources",
  resourceDirectory in Runtime := file(".") / "./src/main/resources",
  addCompilerPlugin(scalafixSemanticdb),
  scalacOptions ++= (
    Seq(
      "-P:semanticdb:synthetics:on",
      "-Yrangepos",
      "-Ywarn-dead-code",
      "-deprecation",
      "-feature"
    )
      ++ sys.env.get("SCALAC_OPTS").getOrElse("").split(" ").toSeq
  ),
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
  target := {
    (ThisBuild / baseDirectory).value / "target" / thisProject.value.id
  }
)

lazy val scalaNativeSettings = Seq(
  crossScalaVersions := List(scala211),
  scalaVersion := scala211,
  nativeLinkStubs := true,
  nativeLinkStubs in runMain := true,
  nativeLinkStubs in Test := true,
  sources in (Compile, doc) := Seq.empty
)

lazy val commonDependencies = Seq(
  libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.0" % Test,
  libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.2.0",
  addCompilerPlugin(scalafixSemanticdb),
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
      case Some((2, n)) if n == 11 => List()
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
)

lazy val commonSettingsAndDependencies = commonSettings ++ commonDependencies

lazy val crossProjectConfig: sbtcrossproject.CrossProject =
  crossProject(JVMPlatform)
    .crossType(CrossType.Pure)
    .jvmSettings(
      crossScalaVersions := versionsJVM
    )

lazy val GOLSettings = Seq(
  assemblyJarName in assembly := "game_of_life.jar",
  mainClass in Compile := Some(
    "fmv1992.fmv1992_scala_utilities.game_of_life.GameOfLife"
  )
)

lazy val uniqSettings = Seq(
  assemblyJarName in assembly := "uniq.jar",
  mainClass in Compile := Some("fmv1992.fmv1992_scala_utilities.uniq.Uniq"),
  mainClass in nativeLink := Some("fmv1992.fmv1992_scala_utilities.uniq.Uniq"),
  selectMainClass in (nativeLink) := Some(
    "fmv1992.fmv1992_scala_utilities.uniq.Uniq"
  )
)

lazy val fmv1992_scala_utilitiesSettings = Seq(
  assemblyJarName in assembly := "root.jar",
  mainClass in Compile := Some("fmv1992.fmv1992_scala_utilities.uniq.Uniq")
)

lazy val util: sbtcrossproject.CrossProject = crossProjectConfig
  .in(file("./src/main/scala/fmv1992/fmv1992_scala_utilities/util"))
  .settings(commonSettingsAndDependencies)
lazy val utilJVM: sbt.Project = util.jvm

lazy val uniq: sbtcrossproject.CrossProject = crossProjectConfig
  .in(file("./src/main/scala/fmv1992/fmv1992_scala_utilities/uniq"))
  .settings(commonSettingsAndDependencies)
  .settings(uniqSettings)
  .dependsOn(util)
lazy val uniqJVM: sbt.Project = uniq.jvm.dependsOn(utilJVM)

lazy val fmv1992_scala_utilities: sbt.Project =
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
      utilJVM,
      uniqJVM
    )
