
scalaVersion := "2.11.12"

name := "trainer"
organization := "ch.epfl.scala"
version := "1.0"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0"

lazy val doobieVersion = "0.7.1"

libraryDependencies ++= Seq(
  "org.tpolecat" %% "doobie-core"     % doobieVersion,
  "org.tpolecat" %% "doobie-postgres" % doobieVersion,
  "org.tpolecat" %% "doobie-specs2"   % doobieVersion
)

val sparkVersion = "2.4.4"

libraryDependencies ++= List(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion
)

val framelessVersion = "0.8.0"

libraryDependencies ++= List(
  "org.typelevel" %% "frameless-dataset" % framelessVersion,
  "org.typelevel" %% "frameless-ml"      % framelessVersion,
  "org.typelevel" %% "frameless-cats"    % framelessVersion  
)

libraryDependencies += "ml.dmlc" % "xgboost4j-spark" % "0.90"
libraryDependencies += "org.jpmml" % "jpmml-sparkml" % "1.5.4"

libraryDependencies += "org.pmml4s" %% "pmml4s" % "0.9.3"


javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}