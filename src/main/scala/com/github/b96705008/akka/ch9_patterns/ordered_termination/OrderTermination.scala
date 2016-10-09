package com.github.b96705008.akka.ch9_patterns.ordered_termination

import akka.actor.{ActorSystem, PoisonPill, Props}
import com.github.b96705008.akka.ch9_patterns.ordered_termination.pattern._

/**
  * Created by roger19890107 on 10/9/16.
  */
object OrderTermination extends App {
  val system = ActorSystem("order-termination")

  val terminator = system.actorOf(Props(new Terminator(Props[Worker], 5)))

  val master = system.actorOf(Props(new Master(terminator)))

  master ! "hello world"

  master ! PoisonPill

//  Thread.sleep(5000)
//
//  system.terminate()
}
