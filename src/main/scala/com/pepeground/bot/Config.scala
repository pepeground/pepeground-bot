package com.pepeground.bot

import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._

object Config {
  lazy val config = ConfigFactory.load()

  object bot {
    lazy val botConfig = config.getConfig("bot")
    lazy val telegramToken: String = botConfig.getString("telegramToken")

    object punctuation {
      lazy val punctuationConfig = botConfig.getConfig("punctuation")

      lazy val endSentence: List[String] = punctuationConfig.getStringList("endSentence").asScala.toList
    }
  }
}