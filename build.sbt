import sbtcrossproject.CrossPlugin.autoImport.crossProject
import sbtcrossproject.CrossPlugin.autoImport.CrossType

ThisBuild / baseVersion := "0.4"

ThisBuild / organization := "org.typelevel"
ThisBuild / organizationName := "Typelevel"

ThisBuild / publishGithubUser := "milanvdm"
ThisBuild / publishFullName := "Milan van der Meer"

ThisBuild / crossScalaVersions := List("3.0.0-M3", "3.0.0-RC1", "2.12.13", "2.13.4")

ThisBuild / spiewakCiReleaseSnapshots := true

ThisBuild / spiewakMainBranches := List("main")

ThisBuild / githubWorkflowBuildPreamble ++=
  Seq(
    WorkflowStep.Sbt(List("scalafmtCheckAll")),
    WorkflowStep.Sbt(List("scalafmtSbtCheck"))
  )

ThisBuild / homepage := Some(url("https://github.com/typelevel/munit-cats-effect"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/typelevel/munit-cats-effect"),
    "git@github.com:typelevel/munit-cats-effect.git"
  )
)

ThisBuild / licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

ThisBuild / scalafmtOnCompile := true

ThisBuild / testFrameworks += new TestFramework("munit.Framework")

ThisBuild / versionIntroduced := Map(
  "3.0.0-M2" -> "1.0.0",
  "3.0.0-M3" -> "1.0.0"
)

ThisBuild / fatalWarningsInCI := false

lazy val root = project
  .in(file("."))
  .aggregate(ce3.jvm, ce3.js, ce2.jvm, ce2.js)
  .enablePlugins(NoPublishPlugin, SonatypeCiReleasePlugin)

lazy val ce3 = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .settings(
    name := "munit-cats-effect-3",
    Compile / unmanagedSourceDirectories += baseDirectory.value / "../../common/shared/src/main/scala",
    Test / unmanagedSourceDirectories += baseDirectory.value / "../../common/shared/src/test/scala"
  )
  .settings(dottyLibrarySettings)
  .settings(dottyJsSettings(ThisBuild / crossScalaVersions))
  .settings(
    libraryDependencies ++= Seq(
      "org.scalameta" %%% "munit" % "0.7.22",
      "org.typelevel" %%% "cats-effect" % "3.0.0-RC2"
    ),
    mimaPreviousArtifacts := Set.empty
  )
  .jsSettings(scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)))

lazy val ce2 = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .settings(
    name := "munit-cats-effect-2",
    Compile / unmanagedSourceDirectories += baseDirectory.value / "../../common/shared/src/main/scala",
    Test / unmanagedSourceDirectories += baseDirectory.value / "../../common/shared/src/test/scala"
  )
  .settings(dottyLibrarySettings)
  .settings(dottyJsSettings(ThisBuild / crossScalaVersions))
  .settings(
    libraryDependencies ++= Seq(
      "org.scalameta" %%% "munit" % "0.7.22",
      "org.typelevel" %%% "cats-effect" % "2.3.3"
    ),
    mimaPreviousArtifacts := Set.empty
  )
  .jsSettings(scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule)))

addCommandAlias("fmt", """scalafmtSbt;scalafmtAll""")
addCommandAlias("fmtCheck", """scalafmtSbtCheck;scalafmtCheckAll""")
