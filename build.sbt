lazy val commons = Subproject.commons
  .settings(libraryDependencies ++= Dependency.commons:_*)

lazy val dbFiller = Subproject.dbFiller
  .dependsOn(commons)

lazy val api = Subproject.api
  .dependsOn(commons)
  .settings(libraryDependencies ++= Dependency.api:_*)

lazy val trainer = Subproject.trainer
  .dependsOn(commons)
  .settings(libraryDependencies ++= Dependency.trainer:_*)

lazy val root = (
  Common.DS2RootProject("root")
    .aggregate(commons, dbFiller, trainer)
)

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-language:higherKinds",
  "-feature",
)