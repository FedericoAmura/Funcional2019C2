package sfpsfiuba

import scala.util.hashing.MurmurHash3

case class SoyRequest(Fecha: String,
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
                      DifSem: Double) {

  private def sanitizeString[T <: Any](s: T): String = s.toString.replace(".0", "")

  def hash: Int = {
    val requestArray: Array[String] = Array(
      this.Fecha,
      sanitizeString(this.Open),
      sanitizeString(this.High),
      sanitizeString(this.Low),
      sanitizeString(this.Last),
      sanitizeString(this.AjDif),
      sanitizeString(this.Mon),
      sanitizeString(this.OIVol),
      sanitizeString(this.OIDif),
      sanitizeString(this.VolOpe),
      this.Unidad,
      sanitizeString(this.DolarBN),
      sanitizeString(this.DolarItau),
      sanitizeString(this.DifSem))

    MurmurHash3.arrayHash(requestArray)
  }
}
