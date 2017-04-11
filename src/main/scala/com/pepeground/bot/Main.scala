package com.pepeground.bot

import scalikejdbc._
import scalikejdbc.config._

object Main extends App {
  override def main(args: Array[String]): Unit = {
    java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"))

    DBs.setupAll()
    Router.run()
  }
}
