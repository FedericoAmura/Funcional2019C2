package com.funcional.soyapi

import cats.Applicative
import cats.implicits._
import io.circe._
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Encoder, Json}
import org.http4s.EntityEncoder
import org.http4s.circe._

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
                           Cierre: Option[Double],
                           AjDif: Double,
                           Mon: String,
                           OIVol: Int,
                           OIDif: Int,
                           VolOpe: Int,
                           Unidad: String,
                           DolarBN: Double,
                           DolarItau: Double,
                           DifSem: Double,
                           Hash: Option[Int]
                          )

  object SoyData {
    implicit def greetingEntityEncoder[F[_] : Applicative]: EntityEncoder[F, SoyData] =
      jsonEncoderOf[F, SoyData]
  }

  def impl[F[_] : Applicative]: SoyApp[F] = new SoyApp[F] {
    def processRequest(n: SoyApp.SoyRequest): F[SoyApp.SoyData] =
      SoyData(Option(1), n.Fecha, n.Open, n.High, n.Low, n.Last, Option(2), n.AjDif, n.Mon, n.OIVol, n.OIDif, n.VolOpe, n.Unidad, n.DolarBN, n.DolarItau, n.DifSem, Option(3)).pure[F]
  }
}