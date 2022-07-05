name         := "awesome-login"
scalaVersion := "3.1.3"

enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, UniversalPlugin)

graalVMNativeImageGraalVersion := Some("22.1.0")

webpack / version := "4.16.1"
useYarn           := true
webpackConfigFile := Some(baseDirectory.value / "webpack.config.js")

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.12" % "test"

val circeVersion = "0.14.1"
libraryDependencies ++= Seq(
  "io.circe" %%% "circe-core",
  "io.circe" %%% "circe-generic",
  "io.circe" %%% "circe-parser"
).map(_ % circeVersion)

scalacOptions ++= Seq(
  "-deprecation",    // Emit warning and location for usages of deprecated APIs.
  "-encoding",
  "utf-8",           // Specify character encoding used by source files.
  "-explaintypes",   // Explain type errors in more detail.
  "-feature",        // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked",      // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings" // Fail the compilation if there are any warnings.
)

topLevelDirectory := None
Universal / mappings ++= (webpack in (Compile, fullOptJS)).value.map { f =>
  // remove the bundler suffix from the file names
  f.data -> f.data.getName.replace("-opt-bundle", "")
}
