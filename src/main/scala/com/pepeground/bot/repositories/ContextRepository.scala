package com.pepeground.bot.repositories

import com.pepeground.bot.Config
import com.redis._

object ContextRepository {
  val clients = new RedisClientPool(Config.bot.redis.host, Config.bot.redis.port, database = 11)

  def updateContext(path: String, words: List[String]): Unit = {
    val ctx: List[String] = getContext(path, 50)

    clients.withClient { client =>
      val filteredWords: List[String] = words.distinct.map(_.toLowerCase).filter(!ctx.contains(_))
      val aggregatedWords: List[String] = (filteredWords ++ ctx).take(50)

      client.pipeline { p =>
        p.del(path)
        p.lpush(path, aggregatedWords.headOption.getOrElse(""), aggregatedWords.tail: _*)
      }
    }
  }

  def getContext(path: String, limit: Int = 50): List[String] = {
    clients.withClient { client =>
      client
        .lrange[String](path, 0, limit)
        .getOrElse(List())
        .filter(_.nonEmpty)
        .map(_.get)
    }
  }
}