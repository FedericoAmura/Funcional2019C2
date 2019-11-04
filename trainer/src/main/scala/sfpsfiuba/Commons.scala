package sfpsfiuba.commons

final case class Row(
    id: Int,
    date: String,
    open: Int,
    high: Int,
    cierre: Double,
    rest: String
)

object Row {
    def apply(csvRow: String): Row = {
        val values = csvRow.split(",").map(_.trim)
        Row(values(0).toInt,
            values(1),
            values(2).toInt,
            values(3).toInt,
            values(4).toDouble,
            values.drop(4).mkString(","))
    }
}