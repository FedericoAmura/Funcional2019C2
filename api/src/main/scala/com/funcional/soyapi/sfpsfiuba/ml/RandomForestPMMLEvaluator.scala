package com.funcional.soyapi.sfpsfiuba.ml

import org.pmml4s.model.Model

import scala.util.{Failure, Success, Try}

object RandomForestPMMLEvaluator {

  def run(dolarBN: Double, dolarItau: Double, difSem: Double): Try[Double] = {
    Try(unsafeRun(dolarBN, dolarItau, difSem)) match {
      case Success(prediction) => Success(prediction.last._2.asInstanceOf[Double])
      case Failure(e) => Failure(e)
    }
  }

  def unsafeRun(dolarBN: Double, dolarItau: Double, difSem: Double): Map[String, Any] = {
    val model = Model.fromFile(sys.env("MODEL_INPUT_PATH"))

    model.predict(Map[String, Double](
      ("DolarBN",dolarBN),
      ("DolarItau",dolarItau),
      ("DifSem",difSem)
    ))
  }
}