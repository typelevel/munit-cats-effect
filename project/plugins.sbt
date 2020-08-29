val scalaJSVersion =
  Option(System.getenv("SCALAJS_VERSION")).getOrElse("1.1.1")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % scalaJSVersion)
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")
addSbtPlugin("com.codecommit" % "sbt-spiewak-sonatype" % "0.15.2")
addSbtPlugin("com.codecommit" % "sbt-github-actions" % "0.9.1")
