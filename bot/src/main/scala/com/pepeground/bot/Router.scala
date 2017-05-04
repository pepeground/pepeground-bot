package com.pepeground.bot

import com.pepeground.bot.handlers._
import info.mukel.telegrambot4s.Implicits._
import info.mukel.telegrambot4s.api._
import info.mukel.telegrambot4s.methods._
import info.mukel.telegrambot4s.models._

import scala.util.Try
import com.typesafe.scalalogging._
import org.slf4j.LoggerFactory

object Router extends TelegramBot with Polling with Commands {
  def token = Config.bot.telegramToken

  override val logger = Logger(LoggerFactory.getLogger(this.getClass))
  private val botName = Config.bot.name.toLowerCase

  override def onMessage(msg: Message): Unit = {
  }

  private def processMessage(msg: Message): Unit = {
    for (text <- msg.text) cleanCmd(text) match {
      case c if expectedCmd(c, "/repost") => RepostHandler(msg).call() match {
        case Some(s: ForwardMessage) =>
          request(s)
          makeResponse(text, SendMessage(msg.sender, "reposted", replyToMessageId = msg.messageId))
        case None =>
      }
      case c if expectedCmd(c, "/get_stats") => GetStatsHandler(msg).call() match {
        case Some(s: String) =>  makeResponse(text, SendMessage(msg.sender, s, replyToMessageId = msg.messageId))
        case None =>
      }
      case c if expectedCmd(c, "/cool_story") => CoolStoryHandler(msg).call() match {
        case Some(s: String) => makeResponse(text, SendMessage(msg.sender, s))
        case None =>
      }
      case c if expectedCmd(c, "/set_gab") =>
        val level: Option[Int] = text.split(" ").take(2) match {
          case Array(_, randomLevel) => Try(randomLevel.toInt).toOption match {
            case Some(l: Int) => Some(l)
            case None => None
          }
          case _ => None
        }

        level match {
          case Some(l: Int) =>
            SetGabHandler(msg).call(l) match {
              case Some(s: String) => makeResponse(text, SendMessage(msg.sender, s, replyToMessageId = msg.messageId))
              case None =>
            }
          case None => makeResponse(text, SendMessage(msg.sender, "Wrong percent", replyToMessageId = msg.messageId))
        }
      case c if expectedCmd(c, "/get_gab") => GetGabHandler(msg).call() match {
        case Some(s: String) => makeResponse(text, SendMessage(msg.sender, s, replyToMessageId = msg.messageId))
        case None =>
      }
      case c if expectedCmd(c, "/ping") => PingHandler(msg).call() match {
        case Some(s: String) => makeResponse(text, SendMessage(msg.sender, s, replyToMessageId = msg.messageId))
        case None =>
      }
      case s =>
        if(!s.startsWith("/")) handleMessage(msg)
    }
  }

  private def cleanCmd(cmd: String): String = cmd.takeWhile(s => s != ' ' ).toLowerCase

  private def expectedCmd(cmd: String, expected: String): Boolean = {
    cmd.split("@") match {
      case Array(c: String, name: String) => c == expected && name.toLowerCase == botName
      case Array(c: String) => c == expected
      case _ => false
    }
  }

  private def handleMessage(msg: Message): Unit = {
    MessageHandler(msg).call() match {
      case Some(res: Either[Option[String], Option[String]]) => res match {
        case Left(s: Option[String]) => if(s.nonEmpty) makeResponse("message", SendMessage(msg.sender, s.get, replyToMessageId = msg.messageId))
        case Right(s: Option[String]) => if(s.nonEmpty) makeResponse("message", SendMessage(msg.sender, s.get))
      }
      case _ =>
    }
  }

  private def makeResponse(context: String, msg: SendMessage): Unit = {
    logger.info("Response for %s, with text: %s".format(context, msg.text))
    request(msg)
  }
}