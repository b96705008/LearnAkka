package com.github.b96705008.akka.ch5_cluster.load_balancing

import akka.actor.{Actor, ActorSystem, Props}
import com.github.b96705008.akka.ch5_cluster.commons.Add
import com.typesafe.config.ConfigFactory

/**
  * Created by roger19890107 on 9/18/16.
  */
class Backend extends Actor {
  def receive = {
    case Add(num1, num2) =>
      println(s"I'm a backend with path: $self and I received add operation.")
  }
}

object Backend {
  def initiate(port: Int): Unit = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.parseString("akka.simple-cluster.conf.roles = [backend]"))
      .withFallback(ConfigFactory.load("loadbalancer"))

    val system = ActorSystem("ClusterSystem", config)

    val backend = system.actorOf(Props[Backend], name = "backend")
  }
}
