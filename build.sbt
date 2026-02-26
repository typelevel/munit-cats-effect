import com.typesafe.tools.mima.core._

ThisBuild / tlBaseVersion := "2.2"

ThisBuild / developers += tlGitHubDev("milanvdm", "Milan van der Meer")
ThisBuild / startYear := Some(2021)

ThisBuild / crossScalaVersions := List("3.3.7", "2.12.21", "2.13.18")

lazy val docs = project
  .in(file("site"))
  .dependsOn(core.jvm)
  .enablePlugins(TypelevelSitePlugin)

lazy val root = tlCrossRootProject.aggregate(core)

lazy val core = crossProject(JSPlatform, JVMPlatform, NativePlatform)
  .crossType(CrossType.Pure)
  .in(file("core"))
  .settings(
    name := "munit-cats-effect",
    libraryDependencies ++= Seq(
      "org.scalameta" %%% "munit" % "1.2.3",
      "org.typelevel" %%% "cats-effect" % "3.7.0-RC1"
    ),
    mimaBinaryIssueFilters ++= Seq(
      // false-positive. methods were deprecated in munit 1.0.4, they are still there but with different signature
      ProblemFilters.exclude[DirectMissingMethodProblem]("munit.CatsEffectAssertions.assertEquals"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("munit.CatsEffectAssertions.assertNoDiff"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("munit.CatsEffectAssertions.fail"),
      ProblemFilters
        .exclude[DirectMissingMethodProblem]("munit.CatsEffectAssertions.failComparison"),
      ProblemFilters.exclude[DirectMissingMethodProblem]("munit.CatsEffectAssertions.failSuite")
    )
  )
  .nativeSettings(
    tlVersionIntroduced := List("2.12", "2.13", "3").map(_ -> "2.2.0").toMap
  )
