import scalajsbundler.sbtplugin.ScalaJSBundlerPlugin.autoImport.useYarn

lazy val commonSettings = Seq(
  organization := "com.mcurse",
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

val scalaTest    = "org.scalatest" %% "scalatest" % "3.2.12" % Test
val tapirVersion = "1.0.1"
val circeVersion = "0.14.1"

val createSamTemplate = taskKey[Unit]("Computes sam template from tapir code")

lazy val rootProject = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    publishArtifact   := false,
    name              := "awesome-login",
    createSamTemplate := Def.taskDyn {
      val log          = sLog.value
      val templatePath = (baseDirectory.value / "template.yaml").toString
      val runtime      = "nodejs16.x"
      val zipLocation  =
        s"nodeJsServer/target/universal/${name.value}-${version.value}.zip"
      val handler      = s"${name.value}.handler"

      Def.task {
        val _ = (createSamTemplateProject / Compile / runMain)
          .toTask(
            s" LambdaSamTemplate $runtime $zipLocation $handler $templatePath"
          )
          .value
        log.info(s"Wrote template to: $templatePath")
        ()
      }
    }.value
  )
  .aggregate(endpoints, nodeJsServer, createSamTemplateProject)

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

lazy val createSamTemplateProject: Project = (project in file("createSamTemplate"))
  .settings(commonSettings: _*)
  .settings(
    name := "createSamTemplate",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %% "tapir-aws-sam" % tapirVersion
    )
  )
  .dependsOn(endpoints)

lazy val domain = (project in file("domain"))
  .settings(commonSettings: _*)
  .settings(
    name                := "domain",
    libraryDependencies += scalaTest,
    libraryDependencies += "org.typelevel" %%% "cats-effect" % "3.3.9"
  )
  .enablePlugins(ScalaJSPlugin)

lazy val nodeJsServer: Project = (project in file("nodeJsServer"))
  .settings(commonSettings: _*)
  .settings(
    name              := "nodeJsServer",
    libraryDependencies ++= Seq(
      "com.softwaremill.sttp.tapir" %%% "tapir-aws-lambda" % tapirVersion
    ),
    webpack / version := "4.16.1",
    useYarn           := true,
    webpackConfigFile := Some(baseDirectory.value / "webpack.config.js"),
    topLevelDirectory := None,
    Universal / mappings ++= (Compile / fullOptJS / webpack).value.map { f =>
      // remove the bundler suffix from the file names
      f.data -> f.data.getName.replace("-opt-bundle", "")
    }
  )
  .dependsOn(endpoints, domain)
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin, UniversalPlugin)
