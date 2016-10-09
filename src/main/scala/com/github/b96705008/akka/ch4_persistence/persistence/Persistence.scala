package com.github.b96705008.akka.ch4_persistence.persistence

import akka.actor.{ActorSystem, Props}

/**
  * Created by roger19890107 on 9/15/16.
  */
object Persistence extends App {
  import Counter._

  val system = ActorSystem("router")

  val counter = system.actorOf(Props[Counter])

  counter ! Cmd(Increment(3))

  counter ! Cmd(Increment(5))

  counter ! Cmd(Decrement(3))

  counter ! "print"

  Thread.sleep(1000)
  system.terminate()
}
