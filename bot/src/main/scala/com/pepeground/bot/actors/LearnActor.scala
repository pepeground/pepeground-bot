package com.pepeground.bot.actors

import akka.actor.Actor
import com.pepeground.bot.Scheduler
import com.pepeground.bot.signals.Tick
import com.pepeground.core.repositories.{LearnItem, LearnQueueRepository}
import com.pepeground.core.services.LearnService
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import scalikejdbc.DB

import scala.concurrent.Future

class LearnActor extends Actor {
  import context.dispatcher
  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  private lazy val learnQueueRepository = LearnQueueRepository

  def receive = {
    case Tick => Future {
      try {
        learnQueueRepository.pop() match {
          case Some(li: LearnItem) => DB localTx { implicit session =>
            new LearnService(li.message, li.chatId).learnPair()
          }
          case None => Thread.sleep(100)
        }
      } catch {
        case e: Throwable => logger.error(e.getMessage)
      } finally {
        Scheduler.learner ! Tick
      }
    }
    case _ => logger.warn("UNKNOWN")
  }
}