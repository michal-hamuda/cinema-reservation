name := "cinema-reservation"

version := "0.1"

scalaVersion := "2.13.8"

lazy val akkaHttpVersion = "10.2.9"
lazy val akkaVersion    = "2.6.18"

fork := true

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
  "io.circe" %% "circe-generic" % "0.13.0",
  "com.rms.miu" %% "slick-cats" % "0.10.4",
  "de.heikoseeberger" %% "akka-http-circe"          % "1.39.2",
  "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
  "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
  "ch.qos.logback"    % "logback-classic"           % "1.2.3",
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "com.softwaremill.macwire" %% "macros" % "2.5.6" % "provided",
  "com.softwaremill.macwire" %% "macrosakka" % "2.5.6" % "provided",
  "com.h2database" % "h2" % "1.4.196",
  "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
  "org.scalatest" %% "scalatest" % "3.2.0" % "test",
  "org.scalamock" %% "scalamock" % "4.4.0" % "test"
)

parallelExecution in Test := false

