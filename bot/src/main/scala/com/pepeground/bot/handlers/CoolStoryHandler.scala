package com.pepeground.bot.handlers

import com.pepeground.core.services.StoryService
import com.bot4s.telegram.models.Message
import scalikejdbc.DBSession

object CoolStoryHandler {
  def apply(message: Message)(implicit session: DBSession): CoolStoryHandler = {
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
