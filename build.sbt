name := "awesome-login"
scalaVersion := "3.1.3"
enablePlugins(GraalVMNativeImagePlugin)

graalVMNativeImageGraalVersion := Some("22.1.0")

scalacOptions ++= Seq(
  "-deprecation",         // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",   // Specify character encoding used by source files.
  "-explaintypes",        // Explain type errors in more detail.
  "-feature",             // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked",           // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings"      // Fail the compilation if there are any warnings.
)

