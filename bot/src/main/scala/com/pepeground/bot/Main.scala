package com.pepeground.bot

import org.flywaydb.core.Flyway
import scalikejdbc.ConnectionPool
import scalikejdbc.config._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object Main extends App {
  override def main(args: Array[String]): Unit = {
    java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"))

    DBs.setupAll()

    val flyway: Flyway = new Flyway()
    val dataSource = ConnectionPool.dataSource(ConnectionPool.DEFAULT_NAME)

    flyway.setDataSource(dataSource)
    flyway.baseline()
    flyway.migrate()

    args.headOption match {
      case Some("learn") => Learn.run
      case Some("cleanup") => CleanUp.run
      case Some("bot") =>
        print("Running bot")
        Await.ready(Router.run(), Duration.Inf)
      case Some(x: String) =>
        print(s"Unknown application argument: ${x}")
        System.exit(1)
      case None =>
        print("Missing application argument")
        System.exit(1)
    }
  }
}
