package com.pepeground.bot.handlers

import info.mukel.telegrambot4s.models.Message
import scalikejdbc.DBSession

object GetGabHandler {
  def apply(message: Message)(implicit session: DBSession): GetGabHandler = {
    new GetGabHandler(message)
  }
}

class GetGabHandler(message: Message)(implicit session: DBSession) extends GenericHandler(message) {
  def call(): Option[String] = {
    super.before()

    Some("Pizdlivost level is on %s".format(chat.randomChance))
  }
}