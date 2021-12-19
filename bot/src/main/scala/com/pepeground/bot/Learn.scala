package com.pepeground.bot

import com.pepeground.core.repositories.LearnItem
import com.pepeground.core.repositories.LearnQueueRepository
import com.pepeground.core.services.LearnService
import scalikejdbc.DB
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

object Learn {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))
  private lazy val learnQueueRepository = LearnQueueRepository

  def run = {
    while (true) {
      try {
        learn
      } catch {
        case e: Throwable =>
          logger.error(e.getMessage)
          Thread.sleep(5000)
      }
    }
  }

  private def learn = {
    learnQueueRepository.pop() match {
      case Some(li: LearnItem) => DB localTx { implicit session =>
        new LearnService(li.message, li.chatId).learnPair()
      }
      case None => Thread.sleep(100)
    }
  }
}
