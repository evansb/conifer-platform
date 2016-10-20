
lazy val commonDependencies = Seq(
  // Akka
  "com.typesafe.akka" %% "akka-actor" % "2.4.11",
  "com.typesafe.akka" %% "akka-cluster" % "2.4.11",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.11",
  "com.typesafe.akka" %% "akka-http-core" % "2.4.11",
  "com.typesafe.akka" %% "akka-stream" % "2.4.11",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.11",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.11",

  // Guice
  "net.codingwell" %% "scala-guice" % "4.1.0",

  // Logging
  "ch.qos.logback" % "logback-classic" % "1.0.9",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0"
)

lazy val commonSettings = Seq(
  organization := "com.evansb",
  version := "0.1.0",
  scalaVersion := "2.11.8",
  libraryDependencies ++= commonDependencies
)

lazy val core = project.
  settings(commonSettings: _*)

lazy val server = project.
  settings(commonSettings: _*).
  dependsOn(core)
  
lazy val client = project.
  settings(commonSettings: _*).
  settings(
    libraryDependencies ++= Seq(
      "com.github.scopt" %% "scopt" % "3.5.0"
    )
  ).
  dependsOn(core)

lazy val tracker = project.
  settings(commonSettings: _*).
  dependsOn(core)

lazy val root = (project in file(".")).
  aggregate(client, server)
