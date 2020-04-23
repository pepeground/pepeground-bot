package com.pepeground.bot.actors

import akka.actor.Actor
import com.pepeground.bot.{Config, Scheduler}
import com.pepeground.bot.signals.Tick
import com.pepeground.core.repositories.PairRepository
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import scalikejdbc.DB

import scala.concurrent.Future

class CleanupActor extends Actor {
  import context.dispatcher

  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def receive = {
    case Tick => Future {
      try {
        logger.info("START REMOVAL")

        DB localTx { implicit session =>
          val removedIds: List[Long] = PairRepository.removeOld(Config.bot.cleanupLimit)

          if (removedIds.isEmpty) {
            logger.info("NOTHING TO REMOVE")
            Thread.sleep(5000)
          } else {
            logger.info("REMOVED PAIRS %s (%s)".format(removedIds.size, removedIds.mkString(", ")))
          }
        }
      } catch {
        case e: Throwable => logger.error(e.getMessage)
      } finally {
        Scheduler.cleaner ! Tick
      }
    }
    case _ => logger.warn("UNKNOWN")
  }
}