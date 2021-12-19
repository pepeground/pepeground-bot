package com.pepeground.bot

import com.pepeground.core.repositories.PairRepository
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import scalikejdbc.DB

object CleanUp {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def run = {
    while (true) {
      try {
        cleanUp
      } catch {
        case e: Throwable =>
          logger.error(e.getMessage)
          Thread.sleep(5000)
      }
    }
  }
  private def cleanUp = {
    DB localTx { implicit session =>
      val removedIds: List[Long] = PairRepository.removeOld(Config.bot.cleanupLimit)

      if (removedIds.isEmpty) {
        logger.info("NOTHING TO REMOVE")
        Thread.sleep(5000)
      } else {
        logger.info("REMOVED PAIRS %s (%s)".format(removedIds.size, removedIds.mkString(", ")))
      }
    }
  }
}
