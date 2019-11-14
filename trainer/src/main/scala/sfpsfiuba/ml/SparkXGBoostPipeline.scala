
package sfpsfiuba.ml

import sfpsfiuba.commons.Row
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.ml.feature.VectorAssembler
import ml.dmlc.xgboost4j.scala.spark.XGBoostRegressor
import org.apache.spark.ml.Pipeline
import org.jpmml.sparkml.PMMLBuilder

import java.io.File

object SparkXGBoostPipeline {

  def run(rows: List[Row]):Unit = {

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


    val xgbParam = Map("eta" -> 0.3,
      "max_depth" -> 6,
      "missing" -> 0.0,
      "objective" -> "reg:squarederror",
      "num_round" -> 10,
      "num_workers" -> 1)

    val vectorAssembler = new VectorAssembler().
      setInputCols(Array("DolarBN", "DolarItau", "DifSem")).
      setOutputCol("features")
    
    val xgbRegressor = new XGBoostRegressor(xgbParam)
      .setFeaturesCol("features")
      .setLabelCol("Cierre")

    val trainInput = vectorAssembler.transform(trainingSet)
    val model = xgbRegressor.fit(trainInput)
    
    val testInput = vectorAssembler.transform(testSet)
    val predictions = model.transform(testInput)
    
    predictions.show()
    
    val schema: StructType = trainingSet.schema;
    
    model.nativeBooster.saveModel("/tmp/model")
    
    // val pipeline = new Pipeline()
    //   .setStages(Array(vectorAssembler, xgbRegressor))
    // val model = pipeline.fit(trainingSet)
    // val builder = new PMMLBuilder(schema, model)
    // builder.buildFile(new File("/workspaces/Funcional2019C2/trainer/model.pmml"))

    spark.stop()
  }
}
