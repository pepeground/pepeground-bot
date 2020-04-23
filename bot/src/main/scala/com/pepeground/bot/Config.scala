package com.pepeground.bot

import com.pepeground.core.CoreConfig

import scala.collection.JavaConverters._

object Config extends CoreConfig{
  object bot {
    private lazy val botConfig = config.getConfig("bot")

    lazy val twitter = botConfig.getBoolean("twitter")

    lazy val asyncLear: Boolean = botConfig.getBoolean("asyncLearn")

    lazy val cleanupLimit: Int = botConfig.getLong("cleanupLimit").toInt
    lazy val repostChatIds: List[Long] = botConfig.getLongList("repostChatIds").asScala.toList.map(_.toLong)
    lazy val repostChatId: Long = botConfig.getLong("repostChatId")
    lazy val telegramToken: String = botConfig.getString("telegramToken")
    lazy val anchors: List[String] = botConfig.getStringList("anchors").asScala.toList
    lazy val name: String = botConfig.getString("name")
  }
}
