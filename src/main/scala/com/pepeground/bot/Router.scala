package com.pepeground.bot

import com.pepeground.bot.handlers.MessageHandler
import info.mukel.telegrambot4s.Implicits._
import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.methods._
import info.mukel.telegrambot4s.models._

object Router extends TelegramBot with Polling with Commands {
  def token = Config.bot.telegramToken

  override def onMessage(msg: Message): Unit = {
    for (text <- msg.text)
      MessageHandler(msg).call() match {
        case Some(s: String) => request(SendMessage(msg.sender, s))
        case _ =>
      }
  }
}