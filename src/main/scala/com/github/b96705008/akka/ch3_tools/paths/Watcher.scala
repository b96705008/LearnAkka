package com.github.b96705008.akka.ch3_tools.paths

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorIdentity, ActorRef, Identify}

/**
  * Created by roger19890107 on 9/15/16.
  */
class Watcher extends Actor {
  var counterRef: ActorRef = _

  val selection = context.actorSelection("/user/counter")

  selection ! Identify(None)

  override def receive: Receive = {
    case ActorIdentity(_, Some(ref)) =>
      println(s"Actor Reference for counter is ${ref}")
    case ActorIdentity(_, None) =>
      println("Actor does not exist.")
  }
}
