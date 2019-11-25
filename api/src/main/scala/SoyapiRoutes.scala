import cats.effect._
import cats.implicits._
import io.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

import sfpsfiuba.{Cierre, DB, SoyRequest}

object SoyapiRoutes {
  implicit val encodeCierre: Encoder[IO[Cierre]] = new Encoder[IO[Cierre]] {
    override def apply(ioc: IO[Cierre]): Json = {
      val c = ioc.unsafeRunSync()
      Json.obj(
        ("cierre", Json.fromDoubleOrNull(c.cierre))
      )
    }
  }

  def soyRoutes[F[_]: Sync](S: SoyApp[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F]{}
    import dsl._

    HttpRoutes.of[F] {
      case req @ GET -> Root / "soy" =>
        for {
          req <- req.as[SoyRequest]
          soyData <- S.processRequest(req)
          resp <- Ok(soyData.asJson)
        } yield resp
    }
  }
}