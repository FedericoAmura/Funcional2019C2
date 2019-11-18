package sfpsfiuba.ml

import org.pmml4s.model.Model

object RandomForestPMMLEvaulator {

  def run(modelPath: String, dolarBN: Double, dolarItau: Double, difSem: Double): Map[String, Any] = {
    val model = Model.fromFile(modelPath)

    val result = model.predict(Map(
      "DolarBN" -> dolarBN,
      "DolarItau" -> dolarItau,
      "DifSem" -> difSem
    ))

    result
  }
}