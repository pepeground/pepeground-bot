package com.pepeground.bot.handlers

import com.pepeground.bot.repositories.ContextRepository
import com.pepeground.bot.services.{LearnService, StoryService}
import info.mukel.telegrambot4s.models.Message

object MessageHandler {
  def apply(message: Message): MessageHandler = {
    new MessageHandler(message)
  }
}

class MessageHandler(message: Message) extends GenericHandler(message) {
  private lazy val learnService: LearnService = new LearnService(words, chat.id)
  private lazy val storyService: StoryService = new StoryService(words, context, chat.id)

  def call(): Option[Either[Option[String], Option[String]]] = {
    super.before()

    if (!hasText || isEdition) return None

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