package sfpsfiuba.commons


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


// object Row {
//     def apply(csvRow: String): Row = {
//         val values = csvRow.split(",").map(_.trim)
//         Row(values(0).toInt,
//             values(1),
//             values(2).toInt,
//             values(3).toInt,
//             values(4).toDouble,
//             values.drop(4).mkString(","))
//     }
// }