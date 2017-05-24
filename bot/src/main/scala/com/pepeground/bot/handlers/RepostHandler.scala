package com.pepeground.bot.handlers

import com.pepeground.bot.Config
import info.mukel.telegrambot4s.methods.ForwardMessage
import info.mukel.telegrambot4s.models.{Message, User}

object RepostHandler {
  def apply(message: Message): RepostHandler = {
    new RepostHandler(message)
  }
}

class RepostHandler(message: Message) extends GenericHandler(message) {
  def call(): Option[ForwardMessage] = {
    super.before()

    if(canRepost) {
      Some(
        ForwardMessage(
          Right(chat.repostChatUsername.get),
          Left(message.chat.id),
          None,
          message.replyToMessage.get.messageId
        )
      )
    } else {
      None
    }
  }

  def canRepost: Boolean = {
    message.replyToMessage match {
      case None => false
      case Some(mo: Message) => mo.from match {
        case None => false
        case Some(u: User) => u.username match {
          case None => false
          case Some(username: String) => true
        }
      }
    }
  }
}
