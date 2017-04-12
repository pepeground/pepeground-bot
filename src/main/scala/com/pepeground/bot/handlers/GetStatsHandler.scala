package com.pepeground.bot.handlers

import com.pepeground.bot.repositories.PairRepository
import info.mukel.telegrambot4s.models.Message
import scalikejdbc._

object GetStatsHandler {
  def apply(message: Message): GetStatsHandler = {
    new GetStatsHandler(message)
  }
}

class GetStatsHandler(message: Message) extends GenericHandler(message) {
  def call(): Option[String] = {
    super.before()

    val count: Int = DB readOnly { implicit session => PairRepository.getPairsCount(chat.id) }

    Some("Known pairs in this chat: %s.".format(count))
  }
}