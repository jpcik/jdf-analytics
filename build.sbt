name := "jdf-analytics"
organization := "ch.hevs"
version := "1.0.0"
scalaVersion := "2.12.3"
  
libraryDependencies ++= Seq(
  "com.github.tototoshi" %% "scala-csv" % "1.3.5",
  "com.github.haifengl" %% "smile-scala" % "1.5.1",
   "io.spray" %%  "spray-json" % "1.3.4",
  "com.github.jsonld-java" % "jsonld-java" % "0.9.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test",
  "junit" % "junit" % "4.12" % "test"
)

resolvers ++= Seq(
  "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.sonatypeRepo("public"),
  "jitpack" at "https://jitpack.io"
)


EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

scalacOptions ++= Seq("-feature","-deprecation")

enablePlugins(JavaAppPackaging)
