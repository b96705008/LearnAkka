package com.github.b96705008.akka.ch3_tools.routing

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, Props}
import com.github.b96705008.akka.ch3_tools.routing.Worker.Work

/**
  * Created by roger19890107 on 9/15/16.
  */
class Router extends Actor {
  var routees: List[ActorRef] = _

  override def preStart(): Unit = {
    routees = List.fill(5)(
      context.actorOf(Props[Worker])
    )
  }

  override def receive: Receive = {
    case msg: Work =>
      println("I'm a Router and I received a Message...")
      routees(util.Random.nextInt(routees.size)) forward msg
  }
}

class RouterGroup(routees: List[String]) extends Actor {
  def receive = {
    case msg: Work =>
      println(s"I'm a Router Group and I receive Work Message...")
      context.actorSelection(routees(util.Random.nextInt(routees.size))) forward msg
  }
}