package sfpsfiuba.ml

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.ml.{Pipeline, PipelineModel, Transformer}
import org.apache.spark.ml.regression.RandomForestRegressionModel

import org.jpmml.sparkml.PMMLBuilder
import org.jpmml.model.MetroJAXBUtil

import frameless.functions._
import frameless.functions.aggregate._
import frameless.TypedDataset
import frameless.syntax._
import frameless.ml._
import frameless.ml.feature._
import frameless.ml.regression._
import org.apache.spark.ml.linalg.Vector

import sfpsfiuba.Row

import cats.effect.{IO, Resource}


object SparkRFPipeline {

  case class TrainingRow(DolarBN: Double, DolarItau: Double, DifSem: Double, Cierre: Double)
  case class Features(DolarBN: Double, DolarItau: Double, DifSem: Double)
  case class DatasetWithFeatures(DolarBN: Double, DolarItau: Double, DifSem: Double, Cierre: Double, features: Vector)
  case class RFInputs(Cierre: Double, features: Vector)
  case class RowPrediction(DolarBN: Double, DolarItau: Double, DifSem: Double, Cierre: Double, features: Vector, prediction: Double)

  def runOnSparkSession(rows: List[Row])(implicit spark: SparkSession): Array[Byte] = {

    import spark.implicits._

    val rowsDs: TypedDataset[Row] = TypedDataset.create(rows)

    val trainingRowDs: TypedDataset[TrainingRow] = rowsDs.select(
      rowsDs('DolarBN),
      rowsDs('DolarItau),
      rowsDs('DifSem),
      rowsDs('Cierre)
    ).as[TrainingRow]

    trainingRowDs.show().run()  // TODO: remove

    val Array(testSet, trainingSet) = trainingRowDs.randomSplit(Array(0.20, 0.80))

    val assembler = TypedVectorAssembler[Features]
    val rf = TypedRandomForestRegressor[RFInputs]

    val featuresDS: TypedDataset[DatasetWithFeatures] = assembler.transform(trainingSet).as[DatasetWithFeatures]

    val model: AppendTransformer[_, _, RandomForestRegressionModel] = rf.fit(featuresDS).run()

    val pipeline = new Pipeline()
      .setStages(Array(assembler.transformer
                        .setOutputCol("features"),  // Hack para que funcione pipeline de spark
                       model.transformer))
    
    // Hack
    val pipelineModel: PipelineModel = pipeline.fit(trainingRowDs.dataset)

    val schema: StructType = trainingRowDs.dataset.schema;
    // val builder: PMMLBuilder = new PMMLBuilder(schema, pipelineModel)

    val builder = new PMMLBuilder(schema, pipelineModel)

    builder.buildByteArray()
  }

  def run(rows: List[Row]): IO[Array[Byte]] = {

    Resource.fromAutoCloseable(IO(
      SparkSession.builder()
          .config("spark.master", "local")
          .config("spark.ui.enabled", "false")
          .config("spark.executor.memory", "2g")
          .config("spark.driver.memory", "2g")
          .appName("trainer")
          .getOrCreate()
      ) 
    )
    .use {
      spark => IO {
        runOnSparkSession(rows)(spark)
      }
    }
  }
}
