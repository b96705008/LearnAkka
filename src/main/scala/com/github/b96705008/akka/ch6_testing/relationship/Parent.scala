package com.github.b96705008.akka.ch6_testing.relationship

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, ActorRefFactory, Props}

/**
  * Created by roger19890107 on 9/25/16.
  */
class Parent(childMaker: ActorRefFactory => ActorRef) extends Actor {
  //val child = context.actorOf(Props[Child])
  val child = childMaker(context)
  var ponged = false

  override def receive: Receive = {
    case "ping" => child ! "ping"
    case "pong" => ponged = true
  }
}
