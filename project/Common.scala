import sbt.{Def, _}
import Keys._

object Common {
  val commonSettings: Seq[Def.Setting[String]] = Seq(
    organization := "ar.edu.uba.fi.sfpfs",
    scalaVersion := "2.12.10",
    version := "1.0"
  )

  def DS2Project(name: String, path: String): Project = (
    Project(name, file(path))
      .settings(commonSettings:_*)
  )

  def DS2SubProject(name:String): Project = (
    DS2Project(name, name)
  )

  def DS2RootProject(name: String): Project = (
    DS2Project(name, ".")
  )
}
