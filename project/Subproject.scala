import sbt._

object Subproject {

  val commons = Common.DS2SubProject("commons")

  val dbFiller = Common.DS2SubProject("dbFiller")

  val trainer = Common.DS2SubProject("trainer")

}
