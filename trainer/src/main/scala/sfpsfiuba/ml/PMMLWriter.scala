package sfpsfiuba.ml

import cats.effect.{IO, Resource}

import java.io.{BufferedOutputStream, FileOutputStream}

object PMMLWriter {
  
  def writeBytes(bytes: Array[Byte], modelPath: String): IO[Unit] = {
    Resource.fromAutoCloseable(IO(
        new BufferedOutputStream(new FileOutputStream(modelPath))
      )
    ).use {
      outputStream => IO {
        outputStream.write(bytes)
      }
    }
  }

}
