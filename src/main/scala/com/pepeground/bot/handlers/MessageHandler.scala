package com.pepeground.bot.handlers

import com.pepeground.bot.entities.ChatEntity
import com.pepeground.bot.repositories.ChatRepository
import com.pepeground.bot.services.{LearnService, StoryService}
import info.mukel.telegrambot4s.models.{Message, MessageEntity, User}

object MessageHandler {
  def apply(message: Message): MessageHandler = {
    new MessageHandler(message)
  }
}

class MessageHandler(message: Message) {
  def call(): Option[String] = {
    if (isChatChanged) {
      ChatRepository.updateChat(chat.id, Option(chatName), migrationId)
    }

    if (!hasText) {
      return None
    }

    if (isEdition) {
      return None
    }

    learnService.learnPair()
    processMessage()
  }

  def processMessage(): Option[String] = {
    if(isPrivate || isReplyToBot || isRandomAnswer) {
      storyService.generate()
    } else {
      None
    }
  }


  def getWords(): List[String] = {
    var textCopy: String = text match {
      case Some(s: String) => s
      case None => return List()
    }

    message.entities match {
      case Some(s: Seq[MessageEntity]) => s.foreach { entity =>
        textCopy = textCopy.replace(textCopy.substring(entity.offset, entity.length), " " * entity.length)
      }
      case _ =>
    }

    val result = textCopy.split(" ").filter(_ != " ").map(_.toLowerCase).toList
    result
  }

  def isChatChanged: Boolean = chatName != chat.name.getOrElse("") || migrationId != telegramId

  def isPrivate: Boolean = message.chat.`type` == "private"

  def isRandomAnswer: Boolean = scala.util.Random.nextInt(100) < chat.randomChance

  def isReplyToBot: Boolean = message.replyToMessage match {
    case Some(r: Message) => r.from match {
      case Some(f: User) => f.username.getOrElse("") == "mrazota_bot"
      case None => false
    }
    case None => false
  }

  def hasEntities: Boolean = message.entities match {
    case Some(s: Seq[MessageEntity]) => s.nonEmpty
    case None => false
  }

  def isEdition: Boolean = message.editDate match {
    case Some(_) => true
    case None => false
  }

  def hasText: Boolean = text match {
    case Some(t: String) => t.nonEmpty
    case None => false
  }

  def isCommand: Boolean = text match {
    case Some(t: String) => t.startsWith("/")
    case None => false
  }

  lazy val learnService: LearnService = new LearnService(words, chat.id)
  lazy val storyService: StoryService = new StoryService(words, List(), chat.id)

  lazy val words: List[String] = getWords()

  lazy val text: Option[String] = message.text

  lazy val chat: ChatEntity = ChatRepository.getOrCreateBy(telegramId, chatName, chatType)
  lazy val telegramId: Long = message.chat.id
  lazy val migrationId: Long = message.migrateToChatId.getOrElse(telegramId)
  lazy val chatType: String = message.chat.`type`
  lazy val chatName: String = message.chat.title.getOrElse(fromUsername)
  private lazy val fromUsername: String = message.from match {
    case Some(u: User) => u.username.getOrElse("Unknown")
    case None => "Unknown"
  }

  private lazy val chatContext: String = s"chat_context/${chat.id}"
}