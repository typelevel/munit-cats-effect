ThisBuild / baseVersion := "1.0"

ThisBuild / organization := "org.typelevel"
ThisBuild / organizationName := "Typelevel"

ThisBuild / publishGithubUser := "milanvdm"
ThisBuild / publishFullName := "Milan van der Meer"

ThisBuild / crossScalaVersions := List("3.0.2", "2.12.15", "2.13.8")

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

ThisBuild / fatalWarningsInCI := false

lazy val root = project
  .in(file("."))
  .aggregate(ce3.jvm, ce3.js, ce2.jvm, ce2.js)
  .enablePlugins(NoPublishPlugin, SonatypeCiReleasePlugin)
  .settings(
    libraryDependencies ++= (
      if (ScalaArtifacts.isScala3(scalaVersion.value)) Nil
      else
        Seq(
          compilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)
        )
    )
  )

lazy val ce3 = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .settings(
    name := "munit-cats-effect-3",
    Compile / unmanagedSourceDirectories += baseDirectory.value / "../../common/shared/src/main/scala",
    Test / unmanagedSourceDirectories += baseDirectory.value / "../../common/shared/src/test/scala"
  )
  .settings(
    libraryDependencies ++= (
      if (ScalaArtifacts.isScala3(scalaVersion.value)) Nil
      else
        Seq(
          compilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)
        )
    ),
    libraryDependencies ++= Seq(
      "org.scalameta" %%% "munit" % "0.7.29",
      "org.typelevel" %%% "cats-effect" % "3.3.5"
    ),
    mimaPreviousArtifacts := Set.empty
  )
  .jvmSettings(
    Compile / unmanagedSourceDirectories += baseDirectory.value / "../../common/jvm/src/main/scala",
    Test / unmanagedSourceDirectories += baseDirectory.value / "../../common/jvm/src/test/scala"
  )
  .jsSettings(
    Compile / unmanagedSourceDirectories += baseDirectory.value / "../../common/js/src/main/scala",
    Test / unmanagedSourceDirectories += baseDirectory.value / "../../common/js/src/test/scala",
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
  )

lazy val ce2 = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .settings(
    name := "munit-cats-effect-2",
    Compile / unmanagedSourceDirectories += baseDirectory.value / "../../common/shared/src/main/scala",
    Test / unmanagedSourceDirectories += baseDirectory.value / "../../common/shared/src/test/scala"
  )
  .settings(
    libraryDependencies ++= (
      if (ScalaArtifacts.isScala3(scalaVersion.value)) Nil
      else
        Seq(
          compilerPlugin("org.typelevel" % "kind-projector" % "0.13.2" cross CrossVersion.full)
        )
    ),
    libraryDependencies ++= Seq(
      "org.scalameta" %%% "munit" % "0.7.29",
      "org.typelevel" %%% "cats-effect" % "2.5.4"
    ),
    mimaPreviousArtifacts := Set.empty
  )
  .jvmSettings(
    Compile / unmanagedSourceDirectories += baseDirectory.value / "../../common/jvm/src/main/scala",
    Test / unmanagedSourceDirectories += baseDirectory.value / "../../common/jvm/src/test/scala"
  )
  .jsSettings(
    Compile / unmanagedSourceDirectories += baseDirectory.value / "../../common/js/src/main/scala",
    Test / unmanagedSourceDirectories += baseDirectory.value / "../../common/js/src/test/scala",
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
  )

addCommandAlias("fmt", """scalafmtSbt;scalafmtAll""")
addCommandAlias("fmtCheck", """scalafmtSbtCheck;scalafmtCheckAll""")
