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

  println("Lei " + rows.length + " rows")


  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",     // driver classname
    "jdbc:postgresql://host.docker.internal:54320/soy",     // connect URL (driver-specific)
    "root",                  // user
    "root",                          // password
    Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )

  val drop =
    sql"""
      DROP TABLE IF EXISTS soy
    """.update.run

  val create =
    sql"""
      CREATE TABLE soy (
        id Int,
        date Text,
        open Float,
        high Float,
        cierre Float,
        rest Text
      )
    """.update.run

  (drop, create).mapN(_ + _).transact(xa).unsafeRunSync

  Update[Row]("INSERT INTO soy VALUES (?, ?, ?, ?, ?, ?)", None).updateMany(rows)
    .transact(xa).unsafeRunSync

  val testQuery = sql"select * from soy limit 1".query[Row].unique
  val y: Row = testQuery.transact(xa).unsafeRunSync

  println("Saqué de la db " + y)

  // val program1 = sql"select 42".query[Int].unique

  // val io1 = program1.transact(xa)

  // val program2 = sql"select id, foo from test where id = 15".query[Test].option
  // println(program2.transact(xa).unsafeRunSync)

}
