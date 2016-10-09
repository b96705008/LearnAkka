package com.github.b96705008.akka.ch9_patterns.ordered_termination.pattern

import akka.actor.Actor
import akka.actor.Actor.Receive

/**
  * Created by roger19890107 on 10/9/16.
  */
class Worker extends Actor {
  override def preStart(): Unit = {
    println(s"${self.path.name} is running")
  }

  override def postStop(): Unit = {
    println(s"${self.path.name} has stopped")
  }

  override def receive: Receive = {
    case msg =>
      println("Cool, I got a message: " + msg)
  }
}
