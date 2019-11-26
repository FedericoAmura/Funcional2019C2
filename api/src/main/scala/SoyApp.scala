import cats._
import cats.effect.IO
import cats.implicits._
import scala.util.{Failure, Success, Try}

import sfpsfiuba.{Cierre, DB, SoyRequest}
import ml.RandomForestPMMLEvaluator

trait SoyApp[F[_]] {
  def processRequest(n: SoyRequest): F[IO[Cierre]]
}

object SoyApp {
  implicit def apply[F[_]](implicit ev: SoyApp[F]): SoyApp[F] = ev

  def impl[F[_] : Applicative]: SoyApp[F] = new SoyApp[F] {

    private def cierre(r: SoyRequest): Try[Double] =
      RandomForestPMMLEvaluator.run(r.DolarBN, r.DolarItau, r.DifSem)

    def processRequest(reqData: SoyRequest): F[IO[Cierre]] = {
      val reqCierre: Try[Double] = cierre(reqData)

      reqCierre match {
        case Success(cierre) => DB.insertAndReturnCierre(reqData, cierre).pure[F]
        case Failure(e) => throw e
      }
    }
  }

}