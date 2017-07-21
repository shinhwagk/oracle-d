name := "server"

scalaVersion in ThisBuild := "2.12.2"

val akkaVersion = "10.0.9"

unmanagedBase := baseDirectory.value / "libs"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaVersion,
  "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.0.0",
  "com.typesafe.play" %% "play-ws-standalone-json" % "1.0.1",
  "org.scalatest" %% "scalatest" % "3.0.3" % "test"
)