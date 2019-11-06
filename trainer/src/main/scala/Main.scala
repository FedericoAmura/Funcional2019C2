import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.implicits._
import doobie.util.ExecutionContexts

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SparkSession

import frameless.functions._
import frameless.functions.aggregate._
import frameless.TypedDataset
import frameless.syntax._
import frameless.ml._
import frameless.ml.feature._
import frameless.ml.regression._
import org.apache.spark.ml.linalg.Vector

import org.apache.log4j.{Level, Logger}

// import frameless._

import sfpsfiuba.commons.Row


object Main extends App {

  val rootLogger = Logger.getRootLogger()
  rootLogger.setLevel(Level.ERROR)

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",     // driver classname
    "jdbc:postgresql://host.docker.internal:54320/soy",     // connect URL (driver-specific)
    "root",                  // user
    "root",                          // password
    Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )

  val rows: List[Row] = 
    sql"select * from soy"
      .query[Row]
      .to[List]
      .transact(xa)
      .unsafeRunSync

  println("Saqué de la db " + rows.length)

  val conf = new SparkConf()
    .setAppName("trainer")
    .setMaster("local")
    .set("spark.ui.enabled", "false")
  //.setMaster("spark://spark-master:7077")
  //.set("spark.driver.host", "172.23.0.4")
  // val sc = new SparkContext(conf)

  implicit val spark = SparkSession.builder().config(conf).appName("trainer").getOrCreate()
  spark.sparkContext.setLogLevel("WARN")

  import spark.implicits._

  implicit val sqlContext = spark.sqlContext
  val dataset: TypedDataset[Row] = TypedDataset.create(rows)
  // val data = Array(1, 2, 3, 4, 5)
  // val distData = sc.parallelize(rows)

  // val res = distData.map((x: Row) => x.id).reduce(_ + _)

  val res = dataset
    .filter(_.Id > 30)
    .agg(avg(dataset('Cierre)))
    .show()
    .run()

  case class TrainingRow(Open: Double, High: Double, Cierre: Double)

  val trainingSet = dataset.select(
    dataset('Open),
    dataset('High),
    dataset('Cierre)
  ).as[TrainingRow]

  case class Features(Open: Double, High: Double)
  
  val assembler = TypedVectorAssembler[Features]
  
  case class DatasetWithFeatures(
    Open: Double,
    Hight: Double,
    Cierre: Double,
    features: Vector
  )
  
  val trainingDataWithFeatures = assembler.transform(trainingSet).as[DatasetWithFeatures]

  case class RFInputs(Cierre: Double, features: Vector)

  val rf = TypedRandomForestRegressor[RFInputs]
  
  val model = rf.fit(trainingDataWithFeatures).run()

  
  val testData = TypedDataset.create(Seq(TrainingRow(1,1,1)))

  val testDataWithFeatures = assembler.transform(testData).as[DatasetWithFeatures]
    
  case class RowPrediction(
    Open: Double,
    High: Double,
    Cierre: Double,
    features: Vector,
    predictedCierre: Double
  )

  val predictions = model.transform(testDataWithFeatures).as[RowPrediction]

  predictions.select(
    predictions.col('Cierre),
    predictions.col('predictedCierre)
  ).show().run()

  spark.sparkContext.stop()
}
