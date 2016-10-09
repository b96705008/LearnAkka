package com.github.b96705008.akka.ch9_patterns.scheduler_messages

import akka.actor.{ActorSystem, Props}

/**
  * Created by roger19890107 on 10/9/16.
  */
object ScheduleApp extends App {
  val system = ActorSystem("Scheduling-Messages")

  //val scheduler = system.actorOf(Props[ScheduleInConstuctor], "schedule-in-constructor")

  val scheduler = system.actorOf(Props[ScheduleInReceive], "schedule-in-receive")

  Thread.sleep(5000)
  system.terminate()
}
