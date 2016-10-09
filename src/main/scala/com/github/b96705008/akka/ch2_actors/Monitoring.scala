package com.github.b96705008.akka.ch2_actors


import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}


class Athena extends Actor {
  override def receive: Receive = {
    case msg =>
      println(s"Athena received ${msg}")
      context.stop(self)
  }
}

class Ares(athena: ActorRef) extends Actor {
  override def preStart(): Unit = {
    context.watch(athena)
  }

  override def postStop(): Unit = {
    println("Ares postStop ...")
  }

  override def receive: Receive = {
    case Terminated =>
      context.stop(self)
  }
}

object Monitoring extends App {
  val system = ActorSystem("Monitoring")

  val athena = system.actorOf(Props[Athena], "athena")

  val ares = system.actorOf(Props(new Ares(athena)))

  athena ! "good bye"
  Thread.sleep(100)

  system.terminate()
}
