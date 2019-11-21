package sfpsfiuba.commons

import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.implicits._
import doobie.util.ExecutionContexts

object DB {

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",     // driver classname
    "jdbc:postgresql://postgres:5432/soy",     // connect URL (driver-specific)
    "root",                  // user
    "root"                          // password
    // Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )

  def insertRows(rows: List[Row]): IO[Int] = {
    val query: String = "INSERT INTO soy VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
    Update[Row](query, None)
      .updateMany(rows)
      .transact(xa)
  }

  def getRows(): List[Row] = {
    sql"select * from soy"
      .query[Row]
      .to[List]
      .transact(xa)
      .unsafeRunSync
  }

}
