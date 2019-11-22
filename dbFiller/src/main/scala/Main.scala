import cats.effect._
import sfpsfiuba.{CSV, DB}

import scala.util.Properties


object Main extends App {
  val filename: String = Properties.envOrElse("TRAIN_FILE_PATH", "./train.csv")

  val program: IO[Unit] = for {
    rows <- CSV.fromPath(filename)
    _ = println("Tengo " + rows.length + " rows para insertar")
    _ <- DB.insertRows(rows)
    _ = println("Listo")
  } yield ()

  program.unsafeRunSync

}
