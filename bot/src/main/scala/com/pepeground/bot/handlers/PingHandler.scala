package com.pepeground.bot.handlers

import info.mukel.telegrambot4s.models.Message
import scalikejdbc.{AutoSession, DBSession}

object PingHandler {
  def apply(message: Message)(implicit session: DBSession = AutoSession): PingHandler = {
    new PingHandler(message)
  }
}

class PingHandler(message: Message)(implicit session: DBSession) extends GenericHandler(message) {
  def call(): Option[String] = {
    super.before()

    Some("Pong.")
  }
}