lazy val core = (projectMatrix in file("."))
  .settings(
    name := "octavo-indexer"
  )
  .jvmPlatform(scalaVersions = Seq("2.13.8"))

ThisBuild / organization := "io.github.hsci-r"

ThisBuild / version := "1.2.7"

ThisBuild / versionScheme := Some("early-semver")

ThisBuild / scalaVersion := "3.1.2"

ThisBuild / libraryDependencies ++= Seq(
  "org.rogach" %% "scallop" % "4.1.0",

  "org.apache.lucene" % "lucene-core" % "8.9.0",
  "io.github.hsci-r" %% "lucene-perfieldpostingsformatordtermvectorscodec" % "1.2.12",
  "org.apache.lucene" % "lucene-analyzers-common" % "8.9.0",
  "joda-time" % "joda-time" % "2.10.10",

  "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.31",
  "junit" % "junit" % "4.13.2" % "test"
)

ThisBuild / publishTo := sonatypePublishToBundle.value

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

