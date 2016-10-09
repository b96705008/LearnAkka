package com.github.b96705008.akka.ch4_persistence.fsm_query

import akka.actor.{ActorSystem, Props}

/**
  * Created by roger19890107 on 9/16/16.
  */
object PersistentFSM extends App {
  import Account._

  val system = ActorSystem("persistent-fsm-actors")

  val account = system.actorOf(Props[Account])

  account ! Operation(1000, CR)

  account ! Operation(10, DR)

  Thread.sleep(1000)

  system.terminate()
}
