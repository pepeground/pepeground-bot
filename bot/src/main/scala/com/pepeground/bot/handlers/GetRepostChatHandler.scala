package com.pepeground.bot.handlers

import info.mukel.telegrambot4s.models.Message

object GetRepostChatHandler {
  def apply(message: Message): GetRepostChatHandler = {
    new GetRepostChatHandler(message)
  }
}

class GetRepostChatHandler(message: Message) extends GenericHandler(message) {
  def call(): Option[String] = {
    super.before()

    chat.repostChatUsername match {
      case Some(username: String) => Some("Pidorskie quote is on %s".format(username))
      case None => None
    }
  }
}
