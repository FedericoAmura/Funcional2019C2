import sbt._
import Keys._

object Common {
  val commonSettings = Seq(
    organization := "ar.edu.uba.fi.sfpfs",
    scalaVersion := "2.12.10",
    version := "1.0"
  )

  def DS2Project(name: String, path: String) = (
    Project(name, file(path))
      .settings(commonSettings:_*)
  )

  def DS2SubProject(name:String) = (
    DS2Project(name, name)
  )

  def DS2RootProject(name: String) = (
    DS2Project(name, ".")
  )
}
