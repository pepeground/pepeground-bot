package com.pepeground.core.repositories

import com.pepeground.core.CoreConfig
import com.redis._

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

case class WordForDeletion(wordId: Long, name: String)

object WordDeletionQueueRepository {
  val clients = new RedisClientPool(CoreConfig.redis.host, CoreConfig.redis.port, database = 11)
  val wordDeletionQueue = "words_for_deletion"

  def push(wordId: Long, name: String): Unit = {
    clients.withClient(cli => cli.lpush(key = wordDeletionQueue, value = WordForDeletion(wordId, name).asJson.noSpaces))
  }

  def pop(): Option[WordForDeletion] = {
    clients.withClient { cli =>
      val el = cli.rpop(wordDeletionQueue)

      el match {
        case Some(str: String) => decode[WordForDeletion](str) match {
          case Right(s: WordForDeletion) => Option(s)
          case Left(_: Error) => None
        }
        case None => None
      }
    }
  }
}