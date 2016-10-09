package com.github.b96705008.akka.ch9_patterns.scheduler_messages

import akka.actor.Actor

import scala.concurrent.duration._
import java.util.Date

import akka.actor.Actor.Receive


/**
  * Created by roger19890107 on 10/9/16.
  */
class ScheduleInConstuctor extends Actor {
  import context._

  val tick = context.system.scheduler.schedule(500 millis, 1 second, self, "tick")

  override def postStop(): Unit = tick.cancel()

  override def receive: Receive = {
    case "tick" =>
      println(s"Cool! I got tick message at ${new Date().toString}")
  }
}

class ScheduleInReceive extends Actor {
  import context._

  override def preStart(): Unit =
    context.system.scheduler.scheduleOnce(500 millis, self, "tick")

  // do not call preStart again
  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {}

  override def receive: Receive = {
    case "tick" =>
      println(s"Cool! I got tick message at ${new Date().toString}")
      context.system.scheduler.scheduleOnce(1 second, self, "tick")
  }
}
