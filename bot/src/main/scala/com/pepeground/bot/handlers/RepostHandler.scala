package com.pepeground.bot.handlers

import com.pepeground.bot.Config
import com.bot4s.telegram.methods.ForwardMessage
import com.bot4s.telegram.models.{Message, User}
import scalikejdbc.DBSession

object RepostHandler {
  def apply(message: Message)(implicit session: DBSession): RepostHandler = {
    new RepostHandler(message)
  }
}

class RepostHandler(message: Message)(implicit session: DBSession) extends GenericHandler(message) {
  def call(): Option[ForwardMessage] = {
    super.before()

    if(canRepost) {
      Some(
        ForwardMessage(
          chat.repostChatUsername.get,
          message.chat.id,
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
          case Some(username: String) => username.toLowerCase == Config.bot.name.toLowerCase
        }
      }
    }
  }
}
