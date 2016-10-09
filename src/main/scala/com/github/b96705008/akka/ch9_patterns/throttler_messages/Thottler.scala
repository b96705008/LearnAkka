package com.github.b96705008.akka.ch9_patterns.throttler_messages

import java.util.Date

import akka.actor.{Actor, ActorSystem, Props}
import com.github.b96705008.akka.ch9_patterns.throttler_messages.pattern.throttle.Throttler._
import com.github.b96705008.akka.ch9_patterns.throttler_messages.pattern.throttle.TimerBasedThrottler

import scala.concurrent.duration._

class Target extends Actor {
  override def receive: Receive = {
    case msg =>
      println(s"[${new Date().toString}] I receive msg: $msg")
  }
}

object ThottlerApp extends App {

  val system = ActorSystem("Thottler-Messages")

  val target = system.actorOf(Props[Target], "target")

  val throttler = system.actorOf(Props(classOf[TimerBasedThrottler], 3 msgsPer(2 second)))

  throttler ! SetTarget(Some(target))

  throttler ! "1"
  throttler ! "2"
  throttler ! "3"
  throttler ! "4"
  throttler ! "5"

  Thread.sleep(5000)
  system.terminate()
}
