package com.funcional.soyapi.sfpsfiuba

import cats.effect._
import com.funcional.soyapi.SoyApp.SoyRequest
import com.funcional.soyapi.sfpsfiuba.Commons.Cierre
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor.Aux

object DB {

  implicit val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)

  val xa: Aux[IO, Unit] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",     // driver classname
    "jdbc:postgresql://postgres:5432/soy",     // connect URL (driver-specific)
    "root",                  // user
    "root"                          // password
    // Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
  )

  def insertRequest(request: SoyRequest, reqHash: Int, reqCierre: Double): IO[Int] = {
    sql"INSERT INTO soy (fecha, open, high, low, last, cierre, ajdif, mon, oivol, oidif, volope, unidad, dolarbn, dolaritau, difsem, hash) VALUES (${request.Fecha}, ${request.Open}, ${request.High}, ${request.Low}, ${request.Last}, ${reqCierre}, ${request.AjDif}, ${request.Mon}, ${request.OIVol}, ${request.OIDif}, ${request.VolOpe}, ${request.Unidad}, ${request.DolarBN}, ${request.DolarItau}, ${request.DifSem}, ${reqHash}) ON CONFLICT (hash) DO NOTHING".update.run.transact(xa)
  }

  def getCierre(rowHash: Int): IO[Cierre] = {
    sql"SELECT cierre FROM soy WHERE hash = $rowHash".query[Cierre].unique.transact(xa)
  }

  def insertAndReturnCierre(data: SoyRequest, hash: Int, cierre: Double): IO[Cierre] = for {
    _ <- insertRequest(data, hash, cierre)
    c <- getCierre(hash)
  } yield c
}