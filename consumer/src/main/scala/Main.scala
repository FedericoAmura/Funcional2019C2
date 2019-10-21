import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.implicits._
import doobie.util.ExecutionContexts


case class Test(id: Int, foo: String)


object Main extends App {

  val csvFile = io.Source.fromFile("./train.csv")
  for (line <- csvFile.getLines.take(5)) {
    val cols = line.split(",").map(_.trim)
    // do whatever you want with the columns here
    println(s"${cols(0)}|${cols(1)}|${cols(2)}|${cols(3)}")
  }
  csvFile.close


  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",     // driver classname
    "jdbc:postgresql://host.docker.internal:5432/sfpsfiuba",     // connect URL (driver-specific)
    "sfpsfiuba",                  // user
    "sfpsfiuba",                          // password
    Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )

  val program1 = sql"select 42".query[Int].unique

  val io1 = program1.transact(xa)

  // val program2 = sql"select id, foo from test where id = 15".query[Test].option
  // println(program2.transact(xa).unsafeRunSync)

}
