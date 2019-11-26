package sfpsfiuba.ml

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.jpmml.sparkml.PMMLBuilder


import frameless.functions._
import frameless.functions.aggregate._
import frameless.TypedDataset
import frameless.syntax._
import frameless.ml._
import frameless.ml.feature._
import frameless.ml.regression._
import org.apache.spark.ml.linalg.Vector

import sfpsfiuba.Row

import java.io.File

object SparkRFPipeline {

  case class TrainingRow(DolarBN: Double, DolarItau: Double, DifSem: Double, Cierre: Double)
  case class Features(DolarBN: Double, DolarItau: Double, DifSem: Double)
  case class DatasetWithFeatures(DolarBN: Double, DolarItau: Double, DifSem: Double, Cierre: Double, features: Vector)
  case class RFInputs(Cierre: Double, features: Vector)
  case class RowPrediction(DolarBN: Double, DolarItau: Double, DifSem: Double, Cierre: Double, features: Vector, prediction: Double)


  class PipelineWrapper (
    assembler: TypedVectorAssembler[Features],
    model: TypedRandomForestRegressor[RFInputs]
  ) {
    val pipeline = new Pipeline()
      .setStages(Array(assembler.transformer
                        .setOutputCol("features"),  // Hack para que funcione pipeline de spark
                      model.estimator))

    def fit(ds: TypedDataset[TrainingRow]): PipelineModel = {
      // Only for typechecking
      val featuresDS: TypedDataset[DatasetWithFeatures] = assembler.transform(ds).as[DatasetWithFeatures]
      val _ = model.fit(featuresDS)

      pipeline.fit(ds.dataset)
    }
  }


  def run(rows: List[Row], modelPath: String): Unit = {

    val conf: SparkConf = new SparkConf()
      .setAppName("trainer")
      .setMaster("local")
      .set("spark.ui.enabled", "false")
      .set("spark.executor.memory", "2g")
      .set("spark.driver.memory", "2g")

    implicit val spark: SparkSession = SparkSession.builder().config(conf).appName("trainer").getOrCreate()

    import spark.implicits._

    val rowsDs: TypedDataset[Row] = TypedDataset.create(rows)

    val trainingRowDs: TypedDataset[TrainingRow] = rowsDs.select(
      rowsDs('DolarBN),
      rowsDs('DolarItau),
      rowsDs('DifSem),
      rowsDs('Cierre)
    ).as[TrainingRow]

    val Array(testSet, trainingSet) = trainingRowDs.randomSplit(Array(0.20, 0.80))

    trainingSet.show().run()

    val assembler = TypedVectorAssembler[Features]
    val rf = TypedRandomForestRegressor[RFInputs]

    
    val pipelineWrapper = new PipelineWrapper(assembler, rf)
    
    val model: PipelineModel = pipelineWrapper.fit(trainingSet)
    
    val predictions = model.transform(testSet.dataset)
    predictions.show()
    
    val schema: StructType = trainingRowDs.dataset.schema;
    val builder: PMMLBuilder = new PMMLBuilder(schema, model)

    builder.buildFile(new File(modelPath))

    spark.stop()
  }
}
