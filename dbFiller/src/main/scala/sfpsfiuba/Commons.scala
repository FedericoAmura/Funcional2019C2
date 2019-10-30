package sfpsfiuba.commons

final case class Row(
    id: Int,
    date: String,
    open: Float,
    high: Float,
    cierre: Float,
    rest: String
)

object Row {
    def apply(csvRow: String): Row = csvRow match {
        case s"$id,$date,$open,$high,$cierre,$rest" 
            => Row(id.toInt, date, open.toFloat, high.toFloat, cierre.toFloat, rest)
        case _ => null
    }
}