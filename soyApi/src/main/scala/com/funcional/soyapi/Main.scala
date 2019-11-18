package com.funcional.soyapi

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._

object Main extends IOApp {
  def run(args: List[String]) =
    SoyapiServer.stream[IO].compile.drain.as(ExitCode.Success)
}