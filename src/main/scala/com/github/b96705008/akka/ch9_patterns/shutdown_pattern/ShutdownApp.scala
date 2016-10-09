package com.github.b96705008.akka.ch9_patterns.shutdown_pattern

import java.util.Date

import akka.actor.{Actor, ActorSystem, PoisonPill, Props}
import akka.actor.Actor.Receive
import com.github.b96705008.akka.ch9_patterns.shutdown_pattern.pattern.{Reaper, ReaperWatched}


class Target extends Actor with ReaperWatched {
  override def receive: Receive = {
    case msg =>
      println(s"[${new Date().toString}]I received a message: $msg")
  }
}

object ShutdownApp extends App {
  val system = ActorSystem("shutdown")

  val reaper = system.actorOf(Props[Reaper], Reaper.name)

  val target = system.actorOf(Props[Target], "target")

  target ! "Hi"

  target ! PoisonPill
}
