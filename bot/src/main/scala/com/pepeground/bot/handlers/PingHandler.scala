package com.pepeground.bot.handlers

import info.mukel.telegrambot4s.models.Message

object PingHandler {
  def apply(message: Message): PingHandler = {
    new PingHandler(message)
  }
}

class PingHandler(message: Message) extends GenericHandler(message) {
  def call(): Option[String] = {
    super.before()

    Some("Pong.")
  }
}