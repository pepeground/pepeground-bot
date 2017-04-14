package com.pepeground.bot

import com.pepeground.core.CoreConfig

import scala.collection.JavaConverters._

object Config extends CoreConfig{
  object bot {
    private lazy val botConfig = config.getConfig("bot")
    lazy val telegramToken: String = botConfig.getString("telegramToken")
    lazy val anchors: List[String] = botConfig.getStringList("anchors").asScala.toList
    lazy val name: String = botConfig.getString("name")
  }
}