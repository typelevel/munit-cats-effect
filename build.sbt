ThisBuild / tlBaseVersion := "1.0"

ThisBuild / developers += tlGitHubDev("milanvdm", "Milan van der Meer")
ThisBuild / startYear := Some(2021)

ThisBuild / crossScalaVersions := List("3.0.2", "2.12.15", "2.13.8")

ThisBuild / tlFatalWarningsInCi := false

lazy val docs = project
  .in(file("site"))
  .dependsOn(ce3.jvm)
  .enablePlugins(TypelevelSitePlugin)

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
      "org.typelevel" %%% "cats-effect" % "3.3.12"
    ),
    // we are checking binary compatibility from the 1.0.6 version
    mimaPreviousArtifacts ~= {
      _.filter { m =>
        VersionNumber(m.revision).matchesSemVer(SemanticSelector(">=1.0.6"))
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
      "org.typelevel" %%% "cats-effect" % "2.5.5"
    ),
    // we are checking binary compatibility from the 1.0.6 version
    mimaPreviousArtifacts ~= {
      _.filter { m =>
        VersionNumber(m.revision).matchesSemVer(SemanticSelector(">=1.0.6"))
      }
    }
  )
  .jvmSettings(
    Compile / unmanagedSourceDirectories += baseDirectory.value / "../../common/jvm/src/main/scala",
    Test / unmanagedSourceDirectories += baseDirectory.value / "../../common/jvm/src/test/scala"
  )
  .jsSettings(
    libraryDependencies += "org.scala-js" %%% "scala-js-macrotask-executor" % "1.0.0",
    Compile / unmanagedSourceDirectories += baseDirectory.value / "../../common/js/src/main/scala",
    Test / unmanagedSourceDirectories += baseDirectory.value / "../../common/js/src/test/scala",
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
  )

addCommandAlias("fmt", """scalafmtSbt;scalafmtAll""")
addCommandAlias("fmtCheck", """scalafmtSbtCheck;scalafmtCheckAll""")
