import sbt.Keys.libraryDependencies

name := """accounting"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers += Resolver.sonatypeRepo("snapshots")

scalaVersion := "2.12.4"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.mockito" % "mockito-all" % "1.9.5" % Test
)

// execute tests on the same jvm
Keys.fork in Test := false
parallelExecution in Test := false
// don't include api docs
sources in (Compile, doc) := Seq.empty
publishArtifact in (Compile, packageDoc) := false

