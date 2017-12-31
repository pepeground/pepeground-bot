package com.pepeground.bot

import com.pepeground.bot.handlers._
import info.mukel.telegrambot4s.Implicits._
import info.mukel.telegrambot4s.methods._
import info.mukel.telegrambot4s.models.{MessageEntity, _}
import info.mukel.telegrambot4s.api.{Polling, TelegramBot, Extractors => $}

import scala.util.{Failure, Success}
import scala.concurrent.Future
import com.typesafe.scalalogging._
import info.mukel.telegrambot4s.api.declarative.{Commands, RegexCommands}
import org.slf4j.LoggerFactory
import scalikejdbc.DB

object Router extends TelegramBot with Polling with Commands with RegexCommands {
  lazy val token = Config.bot.telegramToken

  override val logger = Logger(LoggerFactory.getLogger(this.getClass))
  private val botName = Config.bot.name.toLowerCase

  onCommand("/repost") { implicit msg =>
    RepostHandler(msg).call() match {
      case Some(s: ForwardMessage) =>
        request(s) onComplete {
          case Success(_) => makeResponse(msg.text, SendMessage(msg.source, "reposted", replyToMessageId = msg.messageId))
          case Failure(_) =>
        }
      case None =>
    }
  }

  onCommand("/get_stats") { implicit msg =>
    GetStatsHandler(msg).call() match {
      case Some(s: String) =>  makeResponse(msg.text, SendMessage(msg.source, s, replyToMessageId = msg.messageId))
      case None =>
    }
  }

  onCommand("/cool_story") { implicit msg =>
    CoolStoryHandler(msg).call() match {
      case Some(s: String) => makeResponse(msg.text, SendMessage(msg.source, s))
      case None =>
    }
  }

  onCommand("/ping") { implicit msg =>
    PingHandler(msg).call() match {
      case Some(s: String) => makeResponse(msg.text, SendMessage(msg.source, s, replyToMessageId = msg.messageId))
      case None =>
    }
  }

  onCommand("/set_gab") { implicit msg =>
    withArgs {
      case Seq($.Int(randomLevel)) =>
        SetGabHandler(msg).call(randomLevel) match {
          case Some(s: String) => makeResponse(msg.text, SendMessage(msg.sender, s, replyToMessageId = msg.messageId))
          case None =>
        }
      case _ => makeResponse(msg.text, SendMessage(msg.source, "Wrong percent", replyToMessageId = msg.messageId))
    }
  }

  onCommand("/get_gab") { implicit msg =>
    GetGabHandler(msg).call() match {
      case Some(s: String) => makeResponse(msg.text, SendMessage(msg.source, s, replyToMessageId = msg.messageId))
      case None =>
    }
  }

  onCommand("/get_repost_channel") { implicit msg =>
    GetRepostChatHandler(msg).call() match {
      case Some(s: String) => makeResponse(msg.text, SendMessage(msg.source, s, replyToMessageId = msg.messageId))
      case None =>
    }
  }

  onCommand("/set_repost_channel") { implicit msg =>
    val entity: Option[MessageEntity] = msg.entities match {
      case Some(entities: Seq[MessageEntity]) => entities.find {
        entity: MessageEntity => entity.`type` == MessageEntityType.Mention
      }
      case None => None
    }

    val channelName: Option[String] = entity match {
      case Some(e: MessageEntity) =>
        val offset: Int = e.offset

        msg.text match {
          case Some(s: String) =>
            Some(s.substring(offset, offset + e.length))
          case None =>
            None
        }
      case None => None
    }

    val chatMemberRequest: Option[Future[ChatMember]] = msg.from.flatMap {
      u: User => request(GetChatMember(msg.chat.id, u.id.toLong))
    }

    channelName match {
      case Some(s: String) =>
        SetRepostChatHandler(msg, chatMemberRequest).call(s) match {
          case Some(s: String) => makeResponse(msg.text, SendMessage(msg.source, s, replyToMessageId = msg.messageId))
          case None =>
        }
      case None =>
        makeResponse(msg.text, SendMessage(msg.source, "Channel name required with @ symbol", replyToMessageId = msg.messageId))
    }
  }

  onRegex(""".*""".r) { implicit msg =>
    _ =>
      handleMessage(msg) match {
        case Some(sm: SendMessage) =>
          makeResponse(msg.text, sm)
        case None => Unit
      }
  }


  private def handleMessage(msg: Message): Option[SendMessage] = {
    DB localTx { implicit session =>
      MessageHandler(msg).call() match {
        case Some(res: Either[Option[String], Option[String]]) => res match {
          case Left(s: Option[String]) => if (s.nonEmpty) Some(SendMessage(msg.source, s.get, replyToMessageId = msg.messageId)) else None
          case Right(s: Option[String]) => if (s.nonEmpty) Some(SendMessage(msg.source, s.get)) else None
        }
        case _ => None
      }
    }
  }

  private def makeResponse(context: Option[String], message: SendMessage): Unit = makeResponse(context.getOrElse("unknown"), message)
  private def makeResponse(context: String, msg: SendMessage): Unit = {
    logger.info("Response for %s, with text: %s".format(context, msg.text))
    request(msg)
  }
}
