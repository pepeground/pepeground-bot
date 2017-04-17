package com.pepeground.bot

import akka.actor.{ActorSystem, Props}
import com.pepeground.bot.actors.CleanupActor
import com.pepeground.bot.signals.Tick
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

class Scheduler {
  val system = ActorSystem("scheduler")
  val cleaner = system.actorOf(Props[CleanupActor])
  val scheduler = QuartzSchedulerExtension(system)

  scheduler.schedule("Cleanup", cleaner, Tick)

  def start(): Boolean = {
    scheduler.start()
  }
}