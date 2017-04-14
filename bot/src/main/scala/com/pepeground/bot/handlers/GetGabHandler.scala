package com.pepeground.bot.handlers

import info.mukel.telegrambot4s.models.Message

object GetGabHandler {
  def apply(message: Message): GetGabHandler = {
    new GetGabHandler(message)
  }
}

class GetGabHandler(message: Message) extends GenericHandler(message) {
  def call(): Option[String] = {
    super.before()

    Some("Pizdlivost level is on %s".format(chat.randomChance))
  }
}