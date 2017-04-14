package com.pepeground.bot

import org.flywaydb.core.Flyway
import scalikejdbc.ConnectionPool
import scalikejdbc.config._

object Main extends App {
  override def main(args: Array[String]): Unit = {
    java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"))

    DBs.setupAll()

    val flyway: Flyway = new Flyway()
    val dataSource = ConnectionPool.dataSource(ConnectionPool.DEFAULT_NAME)

    flyway.setDataSource(dataSource)
    flyway.baseline()
    flyway.migrate()

    Router.run()
  }
}
