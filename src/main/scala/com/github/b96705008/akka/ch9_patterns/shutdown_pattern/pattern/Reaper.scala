package com.github.b96705008.akka.ch9_patterns.shutdown_pattern.pattern

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, Terminated}

import scala.collection.mutable.ArrayBuffer


object Reaper {
  val name = "reaper"

  case class WatchMe(ref: ActorRef)
}

class Reaper extends Actor {
  import Reaper._

  val watched = ArrayBuffer.empty[ActorRef]

  def allSoulsReaped() = {
    println("SYSTEM SHUTDOWN START")
    context.system.terminate()
    println("SYSTEM SHUTDOWN END")
  }

  override final def receive: Receive = {
    case WatchMe(ref) =>
      context.watch(ref)
      watched += ref

    case Terminated(ref) =>
      watched -= ref
      if (watched.isEmpty) allSoulsReaped()
  }
}

trait ReaperWatched { this: Actor =>
    override def preStart(): Unit = {
      println("IN PRESTART")
      context.actorSelection("/user/" + Reaper.name) ! Reaper.WatchMe(self)
    }
}
