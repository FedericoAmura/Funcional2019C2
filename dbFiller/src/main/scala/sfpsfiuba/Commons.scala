package sfpsfiuba.commons

import scala.util.hashing.MurmurHash3

final case class Row(
    Id: Int,
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

object Row {

    def rowToHash(row: Array[String]) = {
        MurmurHash3.arrayHash(
            row.drop(1).patch(5, Nil, 1))  // Remove Id and Cierre
    }

    def apply(csvRow: String): Row = {
        val values = csvRow.split(",").map(_.trim)
        Row(values(0).toInt,
            values(1),
            values(2).toDouble,
            values(3).toDouble,
            values(4).toDouble,
            values(5).toDouble,
            values(6).toDouble,
            values(7).toDouble,
            values(8),
            values(9).toInt,
            values(10).toInt,
            values(11).toInt,
            values(12),
            values(13).toDouble,
            if (values(14) == "NA") 0 else values(14).toDouble,
            values(15).toDouble,
            rowToHash(values))
    }
}