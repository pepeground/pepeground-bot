package com.pepeground.core.repositories

import com.pepeground.core.CoreConfig
import com.redis._

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

case class LearnItem(message: List[String], chatId: Long)

object LearnQueueRepository {
  val clients = new RedisClientPool(CoreConfig.redis.host, CoreConfig.redis.port, database = 11)
  val learnQueue = "learn"

  def push(message: List[String], chatId: Long): Unit = {
    clients.withClient(cli => cli.lpush(key = learnQueue, value = LearnItem(message, chatId).asJson.noSpaces))
  }

  def pop(): Option[LearnItem] = {
    clients.withClient { cli =>
      val el = cli.rpop(learnQueue)

      el match {
        case Some(str: String) => decode[LearnItem](str) match {
          case Right(s: LearnItem) => Option(s)
          case Left(_: Error) => None
        }
        case None => None
      }
    }
  }
}