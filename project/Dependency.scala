import sbt._

object Version {
  val doobieVersion = "0.8.4"
  val catsVersion = "2.0.0"
  val sparkVersion = "2.4.4"
  val framelessVersion = "0.8.0"
  val jpmmlSparkmlVersion = "1.5.4"
  val pmml4sVersion = "0.9.3"
}


object Dependency {

  val doobie = Seq(
    "org.tpolecat" %% "doobie-core",
    "org.tpolecat" %% "doobie-postgres",
    "org.tpolecat" %% "doobie-specs2"
  ).map(_ % Version.doobieVersion)

  val cats = Seq(
    "org.typelevel" %% "cats-core"
  ).map(_ % Version.catsVersion)

  val spark = Seq(
    "org.apache.spark" %% "spark-core",
    "org.apache.spark" %% "spark-sql",
    "org.apache.spark" %% "spark-mllib"
  ).map(_ % Version.sparkVersion)

  val frameless = Seq(
    "org.typelevel" %% "frameless-dataset",
    "org.typelevel" %% "frameless-ml",
    "org.typelevel" %% "frameless-cats"
  ).map(_ % Version.framelessVersion)

  val pmmlWriter = Seq(
    "org.jpmml" % "jpmml-sparkml"
  ).map(_ % Version.jpmmlSparkmlVersion)

  val pmmlReader = Seq(
    "org.pmml4s" %% "pmml4s"
  ).map(_ % Version.pmml4sVersion)

  val commons = doobie ++ cats

  val trainer = spark ++ frameless ++ pmmlWriter ++ pmmlReader

}
