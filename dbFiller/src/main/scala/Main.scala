import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.implicits._
import doobie.util.ExecutionContexts

import sfpsfiuba.commons.Row


object Main extends App {


  val csvFile = io.Source.fromFile("./train.csv")

  var rows: List[Row] = List()

  for (line <- csvFile.getLines.drop(1)) {
    val cols = line.split(",").map(_.trim)
    // do whatever you want with the columns here

    rows = Row.apply(line) :: rows
  }
  csvFile.close

  println("" + rows.length + " rows para insertar")


  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", // driver classname
    "jdbc:postgresql://postgres:5432/soy", // connect URL (driver-specific)
    "root", // user
    "root", // password
    Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )

  val insert = Update[Row](
    "INSERT INTO soy VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", None)
    .updateMany(rows)
    .transact(xa)
    .unsafeRunSync

  println("Listo")
}
