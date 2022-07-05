import com.typesafe.sbt.packager.docker.ExecCmd
import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.useYarn

lazy val commonSettings = Seq(
  organization := "com.softwaremill.ts",
  scalaVersion := "2.13.8",
  scalacOptions ++= Seq(
    "-deprecation",    // Emit warning and location for usages of deprecated APIs.
    "-encoding",
    "utf-8",           // Specify character encoding used by source files.
    "-explaintypes",   // Explain type errors in more detail.
    "-feature",        // Emit warning and location for usages of features that should be imported explicitly.
    "-unchecked",      // Enable additional warnings where generated code depends on assumptions.
    "-Xfatal-warnings" // Fail the compilation if there are any warnings.
  )
)

val scalaTest        = "org.scalatest" %% "scalatest" % "3.2.12" % Test
val amazonSdkVersion = "2.15.77"
val tapirVersion     = "1.0.1"
val scalaLogging     = "3.9.2"
val logback          = "1.2.3"
val circeVersion     = "0.14.1"

val deploy = taskKey[Unit]("Builds and uploads a new Docker image, writes the SAM template and deploys it.")

lazy val rootProject = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    publishArtifact := false,
    name            := "awesome-login",
    deploy          := Def.taskDyn {
      val log          = sLog.value
      val templatePath = (baseDirectory.value / "template.yaml").toString
      val runtime      = "nodejs16.x"
      val zipLocation  = s"${name.value}/target/universal/${name.value}-0.1.0-SNAPSHOT.zip"
      val handler      = s"${name.value}.handler"

      Def.task {
        val _ = (runMain in createApi in Compile)
          .toTask(
            s" LamdaSamTemplate $runtime $zipLocation $handler $templatePath"
          )
          .value
        log.info(s"Wrote template to: $templatePath")
        log.info(s"Wrote template to: $version")

        log.info("Running sam ...")
        ()
      }
    }.value
  )
  .aggregate(endpoints, awesomeLogin, createApi)

lazy val endpoints: Project = (project in file("endpoints"))
  .settings(commonSettings: _*)
  .settings(
    name := "endpoints",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %%% "tapir-core"       % tapirVersion,
      "com.softwaremill.sttp.tapir" %%% "tapir-json-circe" % tapirVersion,
      "io.circe"                    %%% "circe-parser"     % circeVersion,
      "io.circe"                    %%% "circe-generic"    % circeVersion
    )
  )
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)

lazy val createApi: Project = (project in file("create-api"))
  .settings(commonSettings: _*)
  .settings(
    name := "create-api",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-aws-sam" % tapirVersion
    )
  )
  .dependsOn(endpoints)

lazy val awesomeLogin: Project = (project in file("awesome-login"))
  .settings(commonSettings: _*)
  .settings(
    name              := "awesome-login",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %%% "tapir-aws-lambda" % tapirVersion,
      scalaTest
    ),
    webpack / version := "4.16.1",
    useYarn           := true,
    webpackConfigFile := Some(baseDirectory.value / "webpack.config.js"),
    topLevelDirectory := None,
    Universal / mappings ++= (webpack in (Compile, fullOptJS)).value.map { f =>
      // remove the bundler suffix from the file names
      f.data -> f.data.getName.replace("-opt-bundle", "")
    }
  )
  .dependsOn(endpoints)
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, UniversalPlugin)
