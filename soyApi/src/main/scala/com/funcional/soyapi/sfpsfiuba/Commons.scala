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
