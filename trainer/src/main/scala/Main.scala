
import sfpsfiuba.commons.{Row,DB}
import sfpsfiuba.ml.SparkRFPipeline
import sfpsfiuba.ml.RandomForestPMMLEvaulator

import org.apache.log4j.Logger
import org.apache.log4j.Level



object Main extends App {

  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  val modelPath = sys.env("MODEL_OUTPUT_PATH")

  val rows: List[Row] = DB.getRows()

  println("Saqué de la db " + rows.length)

  SparkRFPipeline.run(rows, modelPath)

  println("Terminé entrenamiento, ahora voy a evaular")

  RandomForestPMMLEvaulator.run(modelPath)

}