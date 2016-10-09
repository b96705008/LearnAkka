package com.github.b96705008.akka.ch6_testing.relationship

import akka.actor.{Actor, ActorRef}
import akka.actor.Actor.Receive


class Child(parent: ActorRef) extends Actor {
  override def receive: Receive = {
    case "ping" => parent ! "pong"
  }
}
