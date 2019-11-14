
package sfpsfiuba.ml

import org.pmml4s.model.Model

object RandomForestPMMLEvaulator {

  def run(modelPath: String) = {
    val model = Model.fromFile(modelPath)

    val result = model.predict(Map(
      "DolarBN" -> 2.812,
      "DolarItau" -> 2.823,
      "DifSem" -> -233.0
    ))

    println("Predicción: " + result)

  }
}