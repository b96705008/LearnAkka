package com.github.b96705008.akka.ch1_intro

import akka.actor.{Actor, ActorSystem, Props}

// Define Actor Messages
case class WhoToGreet(who: String)

// Define Greeter Actor
class Greeter extends Actor {
  override def receive = {
    case WhoToGreet(who) => println(s"Hello $who")
  }
}

object HelloAkka extends App {
  val system = ActorSystem("Hello-Akka")

  val greeter = system.actorOf(Props[Greeter], "greeter")

  greeter ! WhoToGreet("Akka")
}
