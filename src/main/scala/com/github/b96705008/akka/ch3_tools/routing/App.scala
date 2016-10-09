package com.github.b96705008.akka.ch3_tools.routing

import akka.actor.{ActorSystem, Props}
import com.github.b96705008.akka.ch3_tools.routing.Worker.Work


/**
  * Created by roger19890107 on 9/15/16.
  */
object RouterApp {
  def main(args: Array[String]): Unit = {
    val system = ActorSystem("router")

    //demoRouter(system)
    //demoRouterGroup(system)

    Thread.sleep(100)
    system.terminate()
  }

  def demoRouter(system: ActorSystem): Unit = {
    val router = system.actorOf(Props[Router])

    router ! Work()

    router ! Work()

    router ! Work()
  }

  def demoRouterGroup(system: ActorSystem): Unit = {
    system.actorOf(Props[Worker], "w1")
    system.actorOf(Props[Worker], "w2")
    system.actorOf(Props[Worker], "w3")
    system.actorOf(Props[Worker], "w4")
    system.actorOf(Props[Worker], "w5")

    val workers: List[String] = List(
      "/user/w1",
      "/user/w2",
      "/user/w3",
      "/user/w4",
      "/user/w5"
    )

    val routerGroup = system.actorOf(Props(classOf[RouterGroup], workers))

    routerGroup ! Work()

    routerGroup ! Work()
  }
}
