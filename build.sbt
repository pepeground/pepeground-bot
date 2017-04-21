lazy val commonSettings = Seq(
  organization := "com.pepeground",
  scalaVersion := "2.12.1",
  version := "0.1",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("snapshots"),
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Flyway" at "https://flywaydb.org/repo"
  ),
  parallelExecution in Test := false,
  libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.2",
    "com.typesafe" % "config" % "1.3.1",
    "org.scalikejdbc" %% "scalikejdbc"  % "2.5.1",
    "org.scalikejdbc" %% "scalikejdbc-config" % "2.5.1",
    "org.postgresql" % "postgresql" % "9.4.1212",
    "org.apache.commons" % "commons-dbcp2" % "2.1.1",
    "joda-time" % "joda-time" % "2.9.9",
    "net.debasishg" %% "redisclient" % "3.4",
    "com.typesafe.akka" %% "akka-actor" % "2.4.17",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
    "org.flywaydb" % "flyway-core" % "4.1.2",
    "com.getsentry.raven" % "raven-logback" % "8.0.2",
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  )
)

lazy val core = (project in file("core")).
  settings(
    commonSettings,
    name := "core"
  )

lazy val bot = (project in file("bot")).
  dependsOn(core).
  settings(
    commonSettings,
    name := "bot",
    mainClass in (Compile,run) := Some("com.pepeground.bot.Main"),
    libraryDependencies ++= Seq(
      "com.enragedginger" %% "akka-quartz-scheduler" % "1.6.0-akka-2.4.x",
      "info.mukel" %% "telegrambot4s" % "2.1.0-SNAPSHOT"
    )
  )

lazy val root = (project in file("."))
  .aggregate(bot, core)
