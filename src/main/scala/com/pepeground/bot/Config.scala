package com.pepeground.bot

import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._

object Config {
  lazy val config = ConfigFactory.load()

  object bot {
    lazy val botConfig = config.getConfig("bot")
    lazy val telegramToken: String = botConfig.getString("telegramToken")
    lazy val anchors: List[String] = botConfig.getStringList("anchors").asScala.toList
    lazy val name: String = botConfig.getString("name")

    object redis {
      lazy val redisConfig = botConfig.getConfig("redis")
      lazy val host = redisConfig.getString("host")
      lazy val port = redisConfig.getInt("port")
    }

    object punctuation {
      lazy val punctuationConfig = botConfig.getConfig("punctuation")

      lazy val endSentence: List[String] = punctuationConfig.getStringList("endSentence").asScala.toList
    }
  }
}