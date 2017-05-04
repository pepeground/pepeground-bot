package com.pepeground.bot

import akka.actor.{ActorSystem, Props}
import com.pepeground.bot.actors.{CleanupActor, TwitterActor}
import com.pepeground.bot.signals.Tick
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

class Scheduler {
  val system = Config.scheduler
  val cleaner = system.actorOf(Props[CleanupActor])
  val scheduler = QuartzSchedulerExtension(system)
  val scrubber = Config.scrubber

  scheduler.schedule("Cleanup", cleaner, Tick)
  scheduler.schedule("Tweets", scrubber, Tick)

  def start(): Boolean = {
    scheduler.start()
  }
}