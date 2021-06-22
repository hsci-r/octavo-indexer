name := "octavo-indexer"

organization := "fi.hsci"

version := "1.2.5"

scalaVersion := "2.13.6"

javacOptions ++= Seq("--release", "11")

scalacOptions ++= Seq("-release", "11")

libraryDependencies ++= Seq(
  "org.rogach" %% "scallop" % "4.0.3",

  "org.apache.lucene" % "lucene-core" % "8.9.0",
  "fi.hsci" %% "lucene-perfieldpostingsformatordtermvectorscodec" % "1.2.8",
  "org.apache.lucene" % "lucene-analyzers-common" % "8.9.0",
  "joda-time" % "joda-time" % "2.10.10",

  "org.scala-lang.modules" %% "scala-java8-compat" % "1.0.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.31",
  "junit" % "junit" % "4.13.2" % "test"
)

resolvers ++= Seq(
  Resolver.mavenLocal
)
