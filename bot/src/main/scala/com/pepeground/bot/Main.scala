package com.pepeground.bot

import com.typesafe.scalalogging.Logger
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import scalikejdbc.{ConnectionPool, GlobalSettings, LoggingSQLAndTimeSettings}
import scalikejdbc.config._

object Main extends App {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))
  override def main(args: Array[String]): Unit = {
    java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"))

    DBs.setupAll()

    GlobalSettings.loggingSQLAndTime = LoggingSQLAndTimeSettings(
      enabled = true,
      singleLineMode = true,
      logLevel = 'INFO
    )

    GlobalSettings.queryCompletionListener = (sql: String, params: Seq[Any], millis: Long) => {
      if (millis > 1000L) {
        logger.warn("completion", Map(
          "sql" -> sql,
          "params" -> params.mkString("[", ",", "]"),
          "millis" -> millis))
      }
    }

    val flyway: Flyway = new Flyway()
    val dataSource = ConnectionPool.dataSource(ConnectionPool.DEFAULT_NAME)

    flyway.setDataSource(dataSource)
    flyway.baseline()
    flyway.migrate()

    Scheduler.setup()

    Router.run()
  }
}
