package com.pepeground.bot.handlers

import com.pepeground.core.repositories.ChatRepository
import info.mukel.telegrambot4s.models.{Message, ChatMember}
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

object SetRepostChatHandler {
  def apply(message: Message, chatMemberRequest: Option[Future[ChatMember]]): SetRepostChatHandler = {
    new SetRepostChatHandler(message, chatMemberRequest)
  }
}

class SetRepostChatHandler(message: Message, chatMemberRequest: Option[Future[ChatMember]]) extends GenericHandler(message) {
  final val AdminStatuses = Array("creator", "administrator")

  def call(chatUsername: String): Option[String] = {
    super.before()

    if(canSetRepostChat) {
      ChatRepository.updateRepostChat(chat.id, chatUsername)
      Some(s"Ya wohl, Lord Helmet! Setting repost channel to ${chatUsername}")
    } else {
      None
    }
  }

  def canSetRepostChat: Boolean = {
    chatMemberRequest match {
      case None => false
      case Some(c) => {
        val status = Await.result(c, 1 minute).status
        AdminStatuses.contains(status)
      }
    }
  }
}
