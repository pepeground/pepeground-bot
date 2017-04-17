package com.pepeground.bot.actors

import akka.actor.Actor
import com.pepeground.bot.signals.Tick
import com.pepeground.core.repositories.PairRepository
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import scalikejdbc.DB

class CleanupActor extends Actor {
  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def receive = {
    case Tick => DB localTx { implicit session =>
      logger.info("START REMOVAL")
      val removedIds: List[Long] = PairRepository.removeOld()

      if(removedIds.isEmpty) {
        logger.info("NOTHING TO REMOVE")
      } else {
        logger.info("REMOVED PAIRS %s (%s)".format(removedIds.size, removedIds.mkString(", ")))
      }
    }
    case _ => logger.warn("UNKNOWN")
  }
}