name := "testcanal"

version := "1.0"

scalaVersion := "2.13.5"

lazy val akkaVersion = "2.6.14"
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.2" % Test,
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "2.0.2",
  "com.typesafe.akka" %% "akka-stream" % akkaVersion
)

mainClass in (Compile, run) := Some("com.canal.Main")
mainClass in (Compile, packageBin) := Some("com.canal.Main")

enablePlugins(JavaAppPackaging)
