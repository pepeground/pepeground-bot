package com.pepeground.bot.handlers

import info.mukel.telegrambot4s.models.Message
import scalikejdbc.DBSession

object GetRepostChatHandler {
  def apply(message: Message)(implicit session: DBSession): GetRepostChatHandler = {
    new GetRepostChatHandler(message)
  }
}

class GetRepostChatHandler(message: Message)(implicit session: DBSession) extends GenericHandler(message) {
  def call(): Option[String] = {
    super.before()

    chat.repostChatUsername match {
      case Some(username: String) => Some("Pidorskie quote is on %s".format(username))
      case None => None
    }
  }
}
