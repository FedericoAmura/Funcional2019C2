
scalaVersion := "2.12.10"

name := "trainer"
organization := "ch.epfl.scala"
version := "1.0"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"

lazy val doobieVersion = "0.8.4"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core"     % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-specs2"   % doobieVersion
)

// val sparkVersion = "2.3.0"

libraryDependencies += "org.apache.spark" %% "spark-sql" % "2.4.4"
