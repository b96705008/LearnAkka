package com.github.b96705008.akka.ch9_patterns.ordered_termination.pattern

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef}

/**
  * Created by roger19890107 on 10/9/16.
  */
class Master(terminator: ActorRef) extends Actor {
  import Terminator._

  override def preStart(): Unit = {
    terminator ! GetChildren(self)
  }

  def initialized(kids: Iterable[ActorRef]): Receive = {
    case msg =>
      println(s"Cool, I got a message: $msg")
  }

  def waiting: Receive = {
    case Children(kids) =>
      context.become(initialized(kids))
  }

  override def receive: Receive = waiting
}
