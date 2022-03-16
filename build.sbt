name := "cinema-reservation"

version := "0.1"

scalaVersion := "2.13.8"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.3.2",
  "com.softwaremill.macwire" %% "macros" % "2.3.6" % "provided",
  "com.h2database" % "h2" % "1.4.196",
  "org.scalatest" %% "scalatest" % "3.2.0" % "test",
  "org.scalamock" %% "scalamock" % "4.4.0" % "test"
)

parallelExecution in Test := false

