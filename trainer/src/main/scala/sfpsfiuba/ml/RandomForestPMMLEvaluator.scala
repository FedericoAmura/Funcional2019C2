package sfpsfiuba.ml

import org.pmml4s.model.Model

object RandomForestPMMLEvaluator {

  def run(modelPath: String): Unit = {
    val model = Model.fromFile(modelPath)

    val result = model.predict(Map(
      "DolarBN" -> 2.812,
      "DolarItau" -> 2.823,
      "DifSem" -> -233.0
    ))

    println("Predicción: " + result)

  }
}