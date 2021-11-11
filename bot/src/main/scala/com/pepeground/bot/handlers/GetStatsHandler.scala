package com.pepeground.bot.handlers

import com.pepeground.core.repositories.PairRepository
import com.bot4s.telegram.models.Message
import scalikejdbc._

object GetStatsHandler {
  def apply(message: Message)(implicit session: DBSession): GetStatsHandler = {
    new GetStatsHandler(message)
  }
}

class GetStatsHandler(message: Message)(implicit session: DBSession) extends GenericHandler(message) {
  def call(): Option[String] = {
    super.before()

    val count: Int = DB readOnly { implicit session => PairRepository.getPairsCount(chat.id) }

    Some("Known pairs in this chat: %s.".format(count))
  }
}
