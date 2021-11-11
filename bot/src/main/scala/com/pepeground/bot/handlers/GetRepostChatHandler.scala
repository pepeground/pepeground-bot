package com.pepeground.bot.handlers

import com.bot4s.telegram.models.Message
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
