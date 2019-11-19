import scala.util.Properties
import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.implicits._
import doobie.util.ExecutionContexts

import sfpsfiuba.commons.{Row, DB}
import sfpsfiuba.csv.CSVReader


object Main extends App {
  val filename: String = Properties.envOrElse("TRAIN_FILE_PATH", "./train.csv")

  val program = for {
    rows <- CSVReader.run(filename)
    _ = println("Tengo " + rows.length + " rows para insertar")
    _ <- DB.insertRows(rows)
    _ = println("Listo")
  } yield ()

  program.unsafeRunSync

}
