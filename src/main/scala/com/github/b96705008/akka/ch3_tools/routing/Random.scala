package com.github.b96705008.akka.ch3_tools.routing

import akka.actor.{ActorSystem, Props}
import akka.routing.{FromConfig, RandomGroup}
import com.github.b96705008.akka.ch3_tools.routing.Worker.Work

/**
  * Created by roger19890107 on 9/15/16.
  */
object Random extends App {
  val system = ActorSystem("router")

  val routerPool = system.actorOf(FromConfig.props(Props[Worker]), "random-router-pool")

  println("==routerPool==")
  routerPool ! Work()
  routerPool ! Work()
  routerPool ! Work()
  routerPool ! Work()

  system.actorOf(Props[Worker], "w1")
  system.actorOf(Props[Worker], "w2")
  system.actorOf(Props[Worker], "w3")

  val paths = List("/user/w1", "/user/w2", "/user/w3")

  val routerGroup = system.actorOf(RandomGroup(paths).props(), "random-router-group")

  println("==routerGroup==")
  routerGroup ! Work()
  routerGroup ! Work()
  routerGroup ! Work()
  routerGroup ! Work()

  Thread.sleep(100)
  system.terminate()
}
