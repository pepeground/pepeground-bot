package com.pepeground.bot.handlers

import com.pepeground.core.services.StoryService
import info.mukel.telegrambot4s.models.Message

object CoolStoryHandler {
  def apply(message: Message): CoolStoryHandler = {
    new CoolStoryHandler(message)
  }
}

class CoolStoryHandler(message: Message) extends GenericHandler(message) {
  private lazy val storyService: StoryService = new StoryService(List(), fullContext, chat.id, Some(50))

  def call(): Option[String] = {
    super.before()

    storyService.generate()
  }
}