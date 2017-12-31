package com.pepeground.bot.handlers

import com.pepeground.core.services.StoryService
import info.mukel.telegrambot4s.models.Message
import scalikejdbc.{AutoSession, DBSession}

object CoolStoryHandler {
  def apply(message: Message)(implicit session: DBSession = AutoSession): CoolStoryHandler = {
    new CoolStoryHandler(message)
  }
}

class CoolStoryHandler(message: Message)(implicit session: DBSession) extends GenericHandler(message) {
  private lazy val storyService: StoryService = new StoryService(List(), fullContext, chat.id, Some(50))

  def call(): Option[String] = {
    super.before()

    storyService.generate()
  }
}