import cats.effect.IO
import scala.util.Properties
import org.apache.log4j.Logger
import org.apache.log4j.Level

import sfpsfiuba.DB
import sfpsfiuba.ml.SparkRFPipeline
import sfpsfiuba.ml.RandomForestPMMLEvaluator

object Main extends App {

  Logger.getLogger("org").setLevel(Level.OFF)
  Logger.getLogger("akka").setLevel(Level.OFF)

  val modelPath: String = Properties.envOrElse("MODEL_OUTPUT_PATH", "/tmp/model.pmml")

  val program: IO[Unit] = DB.getRows.map(rows => SparkRFPipeline.run(rows, modelPath))

  program.unsafeRunSync()

  // Only for testing
  RandomForestPMMLEvaluator.run(modelPath)

}
