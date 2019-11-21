package sfpsfiuba.csv

import scala.io.{BufferedSource,Source}
import cats.effect._ 
import sfpsfiuba.commons.Row


object CSV {

  type CSV = IO[List[Row]]
  
  def fromPath(filename: String): CSV = {  // miLift
    val acquire: IO[BufferedSource] = IO(Source.fromFile(filename))
    Resource.fromAutoCloseable(acquire)
      .use { 
        source => IO {
          source
              .getLines
              .drop(1)
              .map(Row.apply)
              .toList
        }
      }
  }
}