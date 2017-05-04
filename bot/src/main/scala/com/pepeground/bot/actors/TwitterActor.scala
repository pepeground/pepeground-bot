package com.pepeground.bot.actors

import akka.actor.{Actor, ActorSystem, Props}
import com.danielasfregola.twitter4s.TwitterRestClient
import com.danielasfregola.twitter4s.entities.{RatedData, Tweet}
import com.pepeground.bot.Config
import com.pepeground.bot.signals.Tick
import com.pepeground.core.entities.SubscriptionEntity
import com.pepeground.core.repositories.SubscriptionRepository
import com.pepeground.core.services.LearnService
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import scalikejdbc.DB

import scala.concurrent.{ExecutionContext, Promise}
import scala.util.{Failure, Success}

class TwitterActor extends Actor {
  import context.dispatcher
  private val client = TwitterRestClient.apply()
  val logger = Logger(LoggerFactory.getLogger(this.getClass))

  def receive = {
    case Tick =>
      DB readOnly { implicit session =>
        SubscriptionRepository.getList().foreach { s =>
          Config.scrubber ! s
        }
      }
    case s: SubscriptionEntity => grabFromTwitter(s)
  }

  def grabFromTwitter(s: SubscriptionEntity): Unit = {
    client.userTimelineForUser(s.name, include_rts=false, exclude_replies=true, since_id = s.sinceId).onComplete {
      case Success(d: RatedData[Seq[Tweet]]) =>
        val tweets: Seq[Tweet] = d.data

        tweets.foreach { t =>
          logger.info(s"Learn tweet: ${t.text}")
          val learnService = new LearnService(cleanWords(t.text), s.chatId)
          learnService.learnPair()
        }

        if (tweets.nonEmpty) SubscriptionRepository.updateSubscription(s.id, tweets.head.id)
      case Failure(_) =>
    }
  }

  def cleanWords(s: String): List[String] = {
    s
      .split("\\s+")
      .filterNot(s => s == " " || s.isEmpty || s.startsWith("@") ||
        s.startsWith("https://") || s.startsWith("http://") || s.startsWith("#"))
      .map(_.toLowerCase)
      .toList
  }
}