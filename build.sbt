ThisBuild / tlBaseVersion := "1.0"

ThisBuild / developers += tlGitHubDev("milanvdm", "Milan van der Meer")
ThisBuild / startYear := Some(2021)

ThisBuild / crossScalaVersions := List("3.1.1", "2.12.15", "2.13.8")

ThisBuild / tlFatalWarningsInCi := false

lazy val root = tlCrossRootProject.aggregate(ce3, ce2)

lazy val ce3 = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Full)
  .settings(
    name := "munit-cats-effect-3",
    Compile / unmanagedSourceDirectories += baseDirectory.value / "../../common/shared/src/main/scala",
    Test / unmanagedSourceDirectories += baseDirectory.value / "../../common/shared/src/test/scala"
  )
  .settings(
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
