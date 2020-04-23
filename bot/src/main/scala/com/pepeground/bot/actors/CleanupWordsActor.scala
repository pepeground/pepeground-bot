package com.pepeground.bot.actors

import akka.actor.Actor
import com.pepeground.bot.Scheduler
import com.pepeground.bot.signals.Tick
import com.pepeground.core.repositories._
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import scalikejdbc.DB

import scala.concurrent.Future
import scala.concurrent.duration._

class CleanupWordsActor extends Actor {
  import context.dispatcher

  val logger = Logger(LoggerFactory.getLogger(this.getClass))
  val wordDeletionQueueRepository = WordDeletionQueueRepository

  def receive = {
    case Tick => delete()
    case _ => logger.warn("UNKNOWN")
  }

  def delete() = Future {
    var nextLaunch = 5 minutes

    try {
      wordDeletionQueueRepository.pop() match {
        case Some(wd: WordForDeletion) => DB localTx { implicit session =>
          logger.warn(s"Word #${wd.wordId} ${wd.name} - performing deletion...")
          WordRepository.deleteById(wd.wordId)
          logger.warn(s"Word #${wd.wordId} ${wd.name} - deletion was finished")

          nextLaunch = 500 millis
        }
        case None =>
          logger.info(s"Nothing to delete")
      }
    } catch {
      case e: Throwable => logger.error(e.getMessage)
    } finally {
      Scheduler.schedulerSystem.scheduler.scheduleOnce(nextLaunch) {
        Scheduler.wordsCleaner ! Tick
      }
    }
  }
}