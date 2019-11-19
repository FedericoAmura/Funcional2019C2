package sfpsfiuba.csv

import scala.io.{BufferedSource,Source}
import cats.effect._ 
import sfpsfiuba.commons.Row


object CSVReader {
  
  def run(filename: String): IO[List[Row]] = {
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