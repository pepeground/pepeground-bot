package com.pepeground.bot.actors

import akka.actor.Actor
import com.pepeground.bot.Scheduler
import com.pepeground.core.entities.WordEntity
import com.pepeground.core.repositories._
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import scalikejdbc.DB

import scala.concurrent.Future

class CollectObsoleteWordsActor extends Actor {
  import context.dispatcher

  val logger = Logger(LoggerFactory.getLogger(this.getClass))
  val wordDeletionQueueRepository = WordDeletionQueueRepository

  def receive = {
    case wId: Option[Long] => wId match {
      case Some(id: Long) => collect(id)
      case None => findFirstWord()
    }
    case _ => logger.warn("UNKNOWN")
  }

  def collect(id: Long) = Future {
    var nextWord: Option[Long] = Option(id)

    try {
      DB localTx { implicit session =>
        nextWord = WordRepository.getNextId(id)

        if (PairRepository.hasWithWordId(id) || ReplyRepository.hasWithWordId(id)) {
          logger.info(s"Word #${id} still used")
        } else {
          WordRepository.getWordById(id) match {
            case Some(we: WordEntity) =>
              wordDeletionQueueRepository.push(id, we.word)

              logger.warn(s"Word #${id} ${we.word} - deletion was scheduled")
            case None =>
              logger.warn(s"Nothing to delete by id #${id}")
          }
        }
      }
    } catch {
      case e: Throwable => logger.error(e.getMessage)
    } finally {
      Scheduler.obsoleteWordsCollector ! nextWord
    }
  }

  def findFirstWord() = Future {
    var word: Option[Long] = None

    try {
      DB localTx { implicit session =>
        WordRepository.getFirstId() match {
          case Some(id: Long) =>
            word = Option(id)
          case None =>
            word = None
        }
      }
    } catch {
      case e: Throwable => logger.error(e.getMessage)
    } finally {
      Scheduler.obsoleteWordsCollector ! word
    }
  }
}