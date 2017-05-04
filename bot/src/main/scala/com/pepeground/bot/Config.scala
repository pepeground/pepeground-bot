package com.pepeground.bot

import akka.actor.{ActorSystem, Props}
import com.pepeground.bot.actors.TwitterActor
import com.pepeground.core.CoreConfig

import scala.collection.JavaConverters._

object Config extends CoreConfig{
  val scheduler = ActorSystem("scheduler")
  val scrubber = scheduler.actorOf(Props[TwitterActor])

  object bot {
    private lazy val botConfig = config.getConfig("bot")
    lazy val repostChatIds: List[Long] = botConfig.getLongList("repostChatIds").asScala.toList.map(_.toLong)
    lazy val repostChatId: Long = botConfig.getLong("repostChatId")
    lazy val telegramToken: String = botConfig.getString("telegramToken")
    lazy val anchors: List[String] = botConfig.getStringList("anchors").asScala.toList
    lazy val name: String = botConfig.getString("name")
  }
}