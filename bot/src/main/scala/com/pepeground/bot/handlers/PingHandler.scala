package com.pepeground.bot.handlers

import info.mukel.telegrambot4s.models.Message
import scalikejdbc.DBSession

object PingHandler {
  def apply(message: Message)(implicit session: DBSession): PingHandler = {
    new PingHandler(message)
  }
}

class PingHandler(message: Message)(implicit session: DBSession) extends GenericHandler(message) {
  def call(): Option[String] = {
    super.before()

    Some("Pong.")
  }
}