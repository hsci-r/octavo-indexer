// To sync with Maven central, you need to supply the following information:
ThisBuild / publishMavenStyle := true

// Open-source license of your choice
ThisBuild / licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))

ThisBuild / homepage := Some(url("https://github.com/hsci-r/octavo-indexer"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/hsci-r/octavo-indexer"),
    "scm:git@github.com:hsci-r/octavo-indexer.git"
  )
)

ThisBuild / developers := List(
  Developer(id="jiemakel", name="Eetu Mäkelä", email="eetu.makela@iki.fi",url=url("https://iki.fi/eetu.makela"))
)

