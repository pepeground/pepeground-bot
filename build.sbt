lazy val root = (project in file(".")).
  settings(
    name := "bot",
    organization := "com.pepeground",
    scalaVersion := "2.12.1",
    version := "0.1",
    mainClass in (Compile,run) := Some("com.pepeground.bot.Main"),
    resolvers += Resolver.sonatypeRepo("snapshots"),
    libraryDependencies ++= Seq(
      "info.mukel" %% "telegrambot4s" % "2.1.0-SNAPSHOT",
      "ch.qos.logback" % "logback-classic" % "1.2.2",
      "com.typesafe" % "config" % "1.3.1",
      "org.scalikejdbc" %% "scalikejdbc"  % "2.5.1",
      "org.scalikejdbc" %% "scalikejdbc-config" % "2.5.1",
      "org.postgresql" % "postgresql" % "9.4.1212",
      "org.apache.commons" % "commons-dbcp2" % "2.1.1",
      "joda-time" % "joda-time" % "2.9.9",
      "net.debasishg" %% "redisclient" % "3.4",
      "com.typesafe.akka" %% "akka-actor" % "2.4.17"
    )
  )
