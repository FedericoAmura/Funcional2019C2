package sfpsfiuba.commons

final case class Row(
    id: Int,
    date: String,
    open: Int,
    high: Int,
    cierre: Float,
    rest: String
)

// object Row {
//     def apply(csvRow: String): Row = csvRow match {
//         case s"$id,$date,$open,$high,$cierre,$rest" 
//             => Row(id.toInt, date, open.toInt, high.toInt, cierre.toFloat, rest)
//         case _ => null
//     }
// }