ThisBuild / scalaVersion := "2.13.3"

lazy val `root` = (project in file("."))
  .settings(
    name := "anagram-finder",
    version := "1.0",
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-explaintypes",
      "-unchecked",
      "-feature",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-Xlint",
      "-Xcheckinit",
      "-Xfatal-warnings",
      "-Wdead-code",
      "-Wextra-implicit",
      "-Wmacros:both",
      "-Wnumeric-widen",
      "-Woctal-literal",
      "-Wunused:imports",
      "-Wunused:patvars",
      "-Wunused:privates",
      "-Wunused:locals",
      "-Wunused:explicits",
      "-Wunused:implicits",
      "-Wunused:params",
      "-Wunused:linted",
      "-Wvalue-discard",
      "-P:bm4:no-tupling:n",
      "-Ymacro-annotations",
      "-Ybackend-parallelism",
      "8" // Enable paralellisation — change to desired number!
    ),
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core"   % "2.2.0",
      "org.typelevel" %% "cats-effect" % "2.2.0",
      "org.scalatest" %% "scalatest"   % "3.2.2" % "test"
    )
  )

Compile / doc / sources := Seq() // disable javadoc generation to speedup stage
Global / onChangedBuildSource := ReloadOnSourceChanges

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
enablePlugins(JavaAppPackaging)
