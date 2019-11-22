package com.funcional.soyapi

import cats._
import cats.effect.IO
import cats.implicits._
import com.funcional.soyapi.sfpsfiuba.Commons.Cierre
import com.funcional.soyapi.sfpsfiuba.DB
import com.funcional.soyapi.sfpsfiuba.ml.RandomForestPMMLEvaluator

import scala.util.hashing.MurmurHash3
import scala.util.{Failure, Success, Try}

trait SoyApp[F[_]] {
  def processRequest(n: SoyApp.SoyRequest): F[IO[Cierre]]
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

  def impl[F[_] : Applicative]: SoyApp[F] = new SoyApp[F] {

    private def sanitizeString[T <: Any](s: T): String = s.toString.replace(".0", "")

    private def hash(request: SoyRequest): Int = {
      val requestArray: Array[String] = Array(
        request.Fecha,
        sanitizeString(request.Open),
        sanitizeString(request.High),
        sanitizeString(request.Low),
        sanitizeString(request.Last),
        sanitizeString(request.AjDif),
        sanitizeString(request.Mon),
        sanitizeString(request.OIVol),
        sanitizeString(request.OIDif),
        sanitizeString(request.VolOpe),
        request.Unidad,
        sanitizeString(request.DolarBN),
        sanitizeString(request.DolarItau),
        sanitizeString(request.DifSem))

      MurmurHash3.arrayHash(requestArray)
    }

    private def cierre(r: SoyRequest): Try[Double] =
      RandomForestPMMLEvaluator.run(r.DolarBN, r.DolarItau, r.DifSem)

    def processRequest(reqData: SoyApp.SoyRequest): F[IO[Cierre]] = {
      val reqHash: Int = hash(reqData)
      val reqCierre: Try[Double] = cierre(reqData)

      reqCierre match {
        case Success(cierre) => DB.insertAndReturnCierre(reqData, reqHash, cierre).pure[F]
        case Failure(e) => throw e
      }
    }
  }

}