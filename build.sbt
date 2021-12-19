lazy val versions = new {
  val akka = "2.4.17"
  val scalikejdbc = "3.3.2"
  val telegramCore = "5.1.0"
  val twitter4s = "5.1"
  val akkaQuartzScheduler = "1.6.0-akka-2.4.x"
  val flyway = "4.1.2"
  val scalaLogging = "3.9.4"
  val logback = "1.2.8"
  val typesafeConfig = "1.3.1"
  val postgresql = "9.4.1212"
  val commonsDbcp2 = "2.1.1"
  val jodaTime = "2.9.9"
  val redisClient = "3.4"
  val sentryLogback = "1.3.0"
  val scalatest = "3.0.1"
  val circle = "0.14.1"
}

lazy val commonSettings = Seq(
  organization := "com.pepeground",
  scalaVersion := "2.12.15",
  version := "0.1",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("snapshots"),
    "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/",
    "Flyway" at "https://flywaydb.org/repo"
  ),
  parallelExecution in Test := false,
  test in assembly := {},
  libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % versions.logback,
    "com.typesafe" % "config" % versions.typesafeConfig,
    "org.scalikejdbc" %% "scalikejdbc"  % versions.scalikejdbc,
    "org.scalikejdbc" %% "scalikejdbc-joda-time"  % versions.scalikejdbc,
    "org.scalikejdbc" %% "scalikejdbc-config" % versions.scalikejdbc,
    "org.postgresql" % "postgresql" % versions.postgresql,
    "org.apache.commons" % "commons-dbcp2" % versions.commonsDbcp2,
    "joda-time" % "joda-time" % versions.jodaTime,
    "net.debasishg" %% "redisclient" % versions.redisClient,
    "com.typesafe.akka" %% "akka-actor" % versions.akka,
    "com.typesafe.scala-logging" %% "scala-logging" % versions.scalaLogging,
    "org.flywaydb" % "flyway-core" % versions.flyway,
    "io.sentry" % "sentry-logback" % versions.sentryLogback,
    "io.circe" %% "circe-core" % versions.circle,
    "io.circe" %% "circe-generic" % versions.circle,
    "io.circe" %% "circe-parser" % versions.circle,
    "org.scalatest" %% "scalatest" % versions.scalatest % "test",
    "org.scalikejdbc" %% "scalikejdbc-test" % versions.scalikejdbc % "test"
  ),
)

lazy val core = (project in file("core")).
  settings(
    commonSettings,
    name := "core",
  )

lazy val bot = (project in file("bot")).
  dependsOn(core).
  settings(
    commonSettings,
    name := "bot",
    mainClass in (Compile,run) := Some("com.pepeground.bot.Main"),
    libraryDependencies ++= Seq(
      "com.enragedginger" %% "akka-quartz-scheduler" % versions.akkaQuartzScheduler,
      "com.danielasfregola" %% "twitter4s" % versions.twitter4s,
      "com.bot4s" %% "telegram-core" % versions.telegramCore,
      "io.micrometer" % "micrometer-registry-prometheus" % "1.4.1",
      "biz.enef" %% "slogging" % "0.6.1"
    ),
    assemblyMergeStrategy := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x =>
        val oldStrategy = (assembly / assemblyMergeStrategy).value
        oldStrategy(x)
    },
  )

lazy val root = (project in file("."))
  .aggregate(bot, core)
