import cats.effect._
import scala.util.Properties

import sfpsfiuba.{CSV, DB}

object Main extends App {
  val filename: String = Properties.envOrElse("TRAIN_FILE_PATH", "./train.csv")

  val program: IO[Unit] = for {
    rows <- CSV.fromPath(filename)
    _ <- DB.insertRows(rows)
  } yield ()

  program.unsafeRunSync

}
