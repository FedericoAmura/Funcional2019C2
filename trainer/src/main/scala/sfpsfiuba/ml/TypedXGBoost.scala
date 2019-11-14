
import frameless.ml.{AppendTransformer,TypedEstimator}
import frameless.ml.internals.TreesInputsChecker
import ml.dmlc.xgboost4j.scala.spark.{XGBoostRegressor,XGBoostRegressionModel}


package frameless {
package ml {
package regression {
      
final class TypedXGBoostRegressor[Inputs] private[ml](
  rf: XGBoostRegressor,
  labelCol: String,
  featuresCol: String
) extends TypedEstimator[Inputs, TypedXGBoostRegressor.Outputs, XGBoostRegressionModel] {

  val estimator: XGBoostRegressor =
    rf
      .setLabelCol(labelCol)
      .setFeaturesCol(featuresCol)
      .setPredictionCol(AppendTransformer.tempColumnName)

  private def copy(newXgb: XGBoostRegressor): TypedXGBoostRegressor[Inputs] =
    new TypedXGBoostRegressor[Inputs](newXgb, labelCol, featuresCol)
}

object TypedXGBoostRegressor {
  case class Outputs(prediction: Double)

  def apply[Inputs](implicit inputsChecker: TreesInputsChecker[Inputs])
  : TypedXGBoostRegressor[Inputs] = {
      val xgbParam = Map("eta" -> 0.3,
        "max_depth" -> 6,
        "missing" -> 0.0,
        "objective" -> "reg:squarederror",
        "num_round" -> 10,
        "num_workers" -> 1)
        
    new TypedXGBoostRegressor(
        new XGBoostRegressor(xgbParam),
        inputsChecker.labelCol,
        inputsChecker.featuresCol)
  }
}

}
}
}