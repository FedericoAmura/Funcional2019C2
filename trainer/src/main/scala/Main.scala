import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.implicits._
import doobie.util.ExecutionContexts

import sfpsfiuba.commons.Row


object Main extends App {


  // implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  // val xa = Transactor.fromDriverManager[IO](
  //   "org.postgresql.Driver",     // driver classname
  //   "jdbc:postgresql://host.docker.internal:54320/soy",     // connect URL (driver-specific)
  //   "root",                  // user
  //   "root",                          // password
  //   Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  // )

  // val testQuery = sql"select * from soy limit 1".query[Row].unique
  // val y: Row = testQuery.transact(xa).unsafeRunSync

  // println("Saqué de la db " + y)

  // val program1 = sql"select 42".query[Int].unique

  // val io1 = program1.transact(xa)

  // val program2 = sql"select id, foo from test where id = 15".query[Test].option
  // println(program2.transact(xa).unsafeRunSync)

}
