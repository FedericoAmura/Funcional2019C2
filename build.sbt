import Common._

lazy val commons = Subproject.commons
  .settings(libraryDependencies ++= Dependency.commons:_*)

lazy val dbFiller = Subproject.dbFiller
  .dependsOn(commons)

lazy val trainer = Subproject.trainer
  .dependsOn(commons)
  .settings(libraryDependencies ++= Dependency.trainer:_*)

lazy val root = (
  Common.DS2RootProject("root")
    .aggregate(commons, dbFiller, trainer)
)
