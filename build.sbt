ThisBuild / tlBaseVersion := "1.0"

ThisBuild / developers += tlGitHubDev("milanvdm", "Milan van der Meer")
ThisBuild / startYear := Some(2021)

ThisBuild / crossScalaVersions := List("3.0.2", "2.12.15", "2.13.8")

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
    // we are checking binary compatibility from the 1.0.6 version
    mimaPreviousArtifacts ~= { _.filter {
      m =>
        val (majorV, minorV, patchV) = {
          val x = m.revision.split("\\.").toList.map(_.toInt)

          (x.headOption, x.lift(1), x.lift(2))
        }

        if (majorV.contains(1) && minorV.contains(0)) patchV.exists(_ >= 6) else true
      }
    }
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
