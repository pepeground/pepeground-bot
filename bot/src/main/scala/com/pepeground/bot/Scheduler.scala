package com.pepeground.bot

import akka.actor.{ActorSystem, Props}
import com.pepeground.bot.actors._
import com.pepeground.bot.signals.Tick
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

import scala.concurrent.duration._

object Scheduler {
  lazy val schedulerSystem = ActorSystem("scheduler")
  lazy val system = schedulerSystem


  lazy val scrubber = schedulerSystem.actorOf(Props[TwitterActor])
  lazy val cleaner = schedulerSystem.actorOf(Props[CleanupActor])
  lazy val learner = schedulerSystem.actorOf(Props[LearnActor])
  lazy val obsoleteWordsCollector = schedulerSystem.actorOf(Props[CollectObsoleteWordsActor])
  lazy val wordsCleaner = schedulerSystem.actorOf(Props[CleanupWordsActor])

  lazy val scheduler = QuartzSchedulerExtension(schedulerSystem)

  import system.dispatcher

  def setup(): Unit = {
    if(Config.bot.asyncLear) {
      schedulerSystem.scheduler.scheduleOnce(500 millis) {
        learner ! Tick
      }
    }

    schedulerSystem.scheduler.scheduleOnce(500 millis) {
      cleaner ! Tick
    }

    schedulerSystem.scheduler.scheduleOnce(500 millis) {
      obsoleteWordsCollector ! None
    }

    schedulerSystem.scheduler.scheduleOnce(500 millis) {
      wordsCleaner ! Tick
    }

    if(Config.bot.twitter) scheduler.schedule("Tweets", scrubber, Tick)
  }
}