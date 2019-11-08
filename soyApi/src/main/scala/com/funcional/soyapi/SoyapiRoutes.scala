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
import org.http4s._
import org.http4s.circe._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import scala.concurrent.ExecutionContext.Implicits.global


object SoyapiRoutes {

  def soyRoutes[F[_]: Sync](S: SoyApp[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._

    HttpRoutes.of[F] {
      case req @ GET -> Root / "soy" =>
        for {
          req <- req.as[SoyApp.SoyRequest]
          soyData <- S.processRequest(req)
          resp <- Ok(soyData.asJson)
        } yield resp
    }
  }
}