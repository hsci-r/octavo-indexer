name := "octavo-indexer"

organization := "fi.hsci"

version := "1.1.5"

scalaVersion := "2.13.5"

idePackagePrefix := Some("fi.hsci")

libraryDependencies ++= Seq(
  "org.rogach" %% "scallop" % "4.0.2",

  "org.apache.lucene" % "lucene-core" % "8.8.1",
  "fi.hsci" %% "lucene-perfieldpostingsformatordtermvectorscodec" % "1.2.1",
  "org.apache.lucene" % "lucene-analyzers-common" % "8.8.1",
  "joda-time" % "joda-time" % "2.10.10",

  "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.0-RC1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.3",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.30",
  "junit" % "junit" % "4.13.2" % "test"
)

resolvers ++= Seq(
  Resolver.mavenLocal
)
