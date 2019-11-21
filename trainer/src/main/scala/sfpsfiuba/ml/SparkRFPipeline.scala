
package sfpsfiuba.ml

import sfpsfiuba.commons.Row
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.regression.RandomForestRegressor
import org.apache.spark.ml.Pipeline
import org.jpmml.sparkml.PMMLBuilder

import java.io.File

object SparkRFPipeline {

  // case class TrainingRow(Open: Double, High: Double, Cierre: Double)
  // case class Features(Open: Double, High: Double)
  // case class DatasetWithFeatures(Open: Double, High: Double, Cierre: Double, features: Vector)
  // case class RFInputs(Cierre: Double, features: Vector)
  // case class RowPrediction(Open: Double, High: Double, Cierre: Double, features: Vector, prediction: Double)

  def run(rows: List[Row], modelPath: String):Unit = {

    val conf = new SparkConf()
      .setAppName("trainer")
      .setMaster("local")
      .set("spark.ui.enabled", "false")
      .set("spark.executor.memory", "2g")
      .set("spark.driver.memory", "2g")

    implicit val spark = SparkSession.builder().config(conf).appName("trainer").getOrCreate()

    import spark.implicits._

    case class TrainingRow(Open: Double, High: Double, Cierre: Double)

    val df = rows.toDS().select("DolarBN", "DolarItau", "DifSem", "Cierre")

    val Array(testSet, trainingSet) = df.randomSplit(Array(0.20, 0.80))

    trainingSet.show()

    val vectorAssembler = new VectorAssembler().
      setInputCols(Array("DolarBN", "DolarItau", "DifSem")).
      setOutputCol("features")

    val rfRegressor = new RandomForestRegressor()
      .setFeaturesCol("features")
      .setLabelCol("Cierre")

    val schema: StructType = trainingSet.schema;

    val pipeline = new Pipeline()
      .setStages(Array(vectorAssembler, rfRegressor))
    val model = pipeline.fit(trainingSet)

    val predictions = model.transform(testSet)
    predictions.show()

    val builder = new PMMLBuilder(schema, model)

    builder.buildFile(new File(modelPath))

    spark.stop()
  }
}
