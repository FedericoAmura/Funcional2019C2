package com.funcional.soyapi

import cats._
import cats.effect._
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import io.circe._
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._
import scala.util.hashing.MurmurHash3

trait SoyApp[F[_]] {
  def processRequest(n: SoyApp.SoyRequest): F[SoyApp.SoyData]
}

object SoyApp {
  implicit def apply[F[_]](implicit ev: SoyApp[F]): SoyApp[F] = ev

  // Request
  final case class SoyRequest(
                               Fecha: String,
                               Open: Double,
                               High: Double,
                               Low: Double,
                               Last: Double,
                               AjDif: Double,
                               Mon: String,
                               OIVol: Int,
                               OIDif: Int,
                               VolOpe: Int,
                               Unidad: String,
                               DolarBN: Double,
                               DolarItau: Double,
                               DifSem: Double
                             )

  // Response
  final case class SoyData(Id: Option[Int],
                           Fecha: String,
                           Open: Double,
                           High: Double,
                           Low: Double,
                           Last: Double,
                           Cierre: Double,
                           AjDif: Double,
                           Mon: String,
                           OIVol: Int,
                           OIDif: Int,
                           VolOpe: Int,
                           Unidad: String,
                           DolarBN: Double,
                           DolarItau: Double,
                           DifSem: Double,
                           Hash: Int
                          )

  object SoyData {
    implicit def greetingEntityEncoder[F[_] : Applicative]: EntityEncoder[F, SoyData] =
      jsonEncoderOf[F, SoyData]
  }

  def impl[F[_] : Applicative]: SoyApp[F] = new SoyApp[F] {
    implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

    val xa = Transactor.fromDriverManager[IO](
      "org.postgresql.Driver", // driver classname
      "jdbc:postgresql://postgres:5432/soy", // connect URL (driver-specific)
      "root", // user
      "root", // password
      Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
    )

    def sanitizeString(s: String): String = {
      s.replace(".0", "")
    }

    def hash(request: SoyRequest): Int = {
      val requestArray: Array[String] = Array(
        request.Fecha,
        sanitizeString(request.Open.toString()),
        sanitizeString(request.High.toString()),
        sanitizeString(request.Low.toString()),
        sanitizeString(request.Last.toString()),
        sanitizeString(request.AjDif.toString()),
        sanitizeString(request.Mon),
        sanitizeString(request.OIVol.toString()),
        sanitizeString(request.OIDif.toString()),
        sanitizeString(request.VolOpe.toString()),
        request.Unidad,
        sanitizeString(request.DolarBN.toString()),
        sanitizeString(request.DolarItau.toString()),
        sanitizeString(request.DifSem.toString()))

      MurmurHash3.arrayHash(requestArray)
    }

    def processRequest(request: SoyApp.SoyRequest): F[SoyApp.SoyData] = {
      val reqHash: Int = hash(request)
      val cierre = 5

      val insert = for {
        _ <- sql"INSERT INTO soy (fecha, open, high, low, last, cierre, ajdif, mon, oivol, oidif, volope, unidad, dolarbn, dolaritau, difsem, hash) VALUES (${request.Fecha}, ${request.Open}, ${request.High}, ${request.Low}, ${request.Last}, ${cierre}, ${request.AjDif}, ${request.Mon}, ${request.OIVol}, ${request.OIDif}, ${request.VolOpe}, ${request.Unidad}, ${request.DolarBN}, ${request.DolarItau}, ${request.DifSem}, ${reqHash}) ON CONFLICT (hash) DO NOTHING".update.run
        s <- sql"SELECT * FROM soy WHERE hash = $reqHash".query[SoyData].unique
      } yield s

      val soyData = insert.transact(xa)
      soyData.unsafeRunSync().pure[F]
    }
  }


}