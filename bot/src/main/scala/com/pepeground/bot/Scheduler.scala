package com.pepeground.bot

import akka.actor.{ActorSystem, Props}
import com.pepeground.bot.actors.{CleanupActor, TwitterActor}
import com.pepeground.bot.signals.Tick
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

object Scheduler {
  lazy val schedulerSystem = ActorSystem("scheduler")

  lazy val scrubber = schedulerSystem.actorOf(Props[TwitterActor])
  lazy val cleaner = schedulerSystem.actorOf(Props[CleanupActor])

  lazy val scheduler = QuartzSchedulerExtension(schedulerSystem)

  def setup(): Unit = {
    scheduler.schedule("Cleanup", cleaner, Tick)
    if(Config.bot.twitter) scheduler.schedule("Tweets", scrubber, Tick)
  }
}