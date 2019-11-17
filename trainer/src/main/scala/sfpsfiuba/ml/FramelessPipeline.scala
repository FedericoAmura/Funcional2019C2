
package sfpsfiuba.ml

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

import frameless.ml.regression.TypedXGBoostRegressor
import sfpsfiuba.commons.Row


case class TrainingRow(Open: Double, High: Double, Cierre: Double)
case class Features(Open: Double, High: Double)
case class DatasetWithFeatures(Open: Double, High: Double, Cierre: Double, features: Vector)
case class RFInputs(Cierre: Double, features: Vector)
case class RowPrediction(Open: Double, High: Double, Cierre: Double, features: Vector, prediction: Double)

object FramelessPipeline {

  def run(rows: List[Row]):Unit = {

    val conf = new SparkConf()
      .setAppName("trainer")
      .setMaster("local")
      .set("spark.ui.enabled", "false")
      .set("spark.executor.memory", "2g")
      .set("spark.driver.memory", "2g")

    implicit val spark = SparkSession.builder().config(conf).appName("trainer").getOrCreate()
    spark.sparkContext.setLogLevel("WARN")

    import spark.implicits._

    implicit val sqlContext = spark.sqlContext
    val dataset: TypedDataset[Row] = TypedDataset.create(rows)

    val res = dataset
      .filter(_.Id > 30)
      .agg(avg(dataset('Cierre)))
      .show()
      .run()

    val trainingSet = dataset.select(
      dataset('Open),
      dataset('High),
      dataset('Cierre)
    ).as[TrainingRow]

    val assembler = TypedVectorAssembler[Features]

    val trainingDataWithFeatures = assembler.transform(trainingSet).as[DatasetWithFeatures]

    val xgb = TypedXGBoostRegressor[RFInputs]

    println("Fiteo")
  
    val model = xgb.fit(trainingDataWithFeatures).run()
  
    println("Fiteado")
    
    val testData = TypedDataset.create(Seq(TrainingRow(1,1,1)))
  
    val testDataWithFeatures = assembler.transform(testData).as[DatasetWithFeatures]
    
    println("transformo")
    val predictions = model.transform(testDataWithFeatures).as[RowPrediction]
  
    println("transformé")
    // predictions.select(
    //   predictions.col('Cierre),
    //   predictions.col('prediction)
    // ).show().run()
  
    predictions.show().run()
  
    println("lesto")
  
    spark.sparkContext.stop()
  }

}