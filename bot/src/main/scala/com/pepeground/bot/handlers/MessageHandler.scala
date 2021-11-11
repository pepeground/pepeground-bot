package com.pepeground.bot.handlers

import com.pepeground.core.repositories.ContextRepository
import com.pepeground.core.services.{LearnService, StoryService}
import com.bot4s.telegram.models.Message
import com.typesafe.scalalogging._
import org.slf4j.LoggerFactory
import scalikejdbc.DBSession

object MessageHandler {
  def apply(message: Message)(implicit session: DBSession): MessageHandler = {
    new MessageHandler(message)
  }
}

class MessageHandler(message: Message)(implicit session: DBSession) extends GenericHandler(message) {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))
  private lazy val learnService: LearnService = new LearnService(words, chat.id)
  private lazy val storyService: StoryService = new StoryService(words, context, chat.id)

  def call(): Option[Either[Option[String], Option[String]]] = {
    super.before()

    if (!hasText || isEdition) return None

    logger.info("Message received: %s from %s (%s)".format(message.text.getOrElse(""), chatName, migrationId))

    learnService.learnPair()
    ContextRepository.updateContext(chatContext, words)

    if (isReplyToBot) return Option(Left(storyService.generate()))
    if (isMentioned) return Option(Left(storyService.generate()))
    if (isPrivate) return Option(Right(storyService.generate()))
    if (hasAnchors) return Option(Right(storyService.generate()))
    if (isRandomAnswer) return Option(Right(storyService.generate()))

    None
  }
}
