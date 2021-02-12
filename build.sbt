name := "octavo-indexer"

organization := "fi.hsci"

version := "1.0.0"

scalaVersion := "2.13.4"

idePackagePrefix := Some("fi.hsci")

libraryDependencies ++= Seq(
  "org.rogach" %% "scallop" % "3.3.1",

  "org.apache.lucene" % "lucene-core" % "8.0.0",
  "fi.seco" %% "lucene-perfieldpostingsformatordtermvectorscodec" % "1.1.5",
  "org.apache.lucene" % "lucene-analyzers-common" % "8.0.0",
  "joda-time" % "joda-time" % "2.10.8",

  "org.scala-lang.modules" %% "scala-java8-compat" % "0.9.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.30",
  "junit" % "junit" % "4.13.1" % "test"
)

resolvers ++= Seq(
  Resolver.mavenLocal
)
