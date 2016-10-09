package com.github.b96705008.akka.ch5_cluster.cluster

import akka.actor.{Actor, ActorSystem, Props, RootActorPath}
import akka.cluster.Cluster
import akka.cluster.ClusterEvent.MemberUp
import com.github.b96705008.akka.ch5_cluster.commons._
import com.typesafe.config.ConfigFactory

class Backend extends Actor {
  val cluster = Cluster(context.system)

  override def preStart(): Unit = {
    cluster.subscribe(self, classOf[MemberUp])
  }

  override def postStop(): Unit = {
    cluster.unsubscribe(self)
  }

  override def receive: Receive = {
    case Add(num1, num2) =>
      println(s"I'm a backend with path: $self and I received add operation.")
    case MemberUp(member) =>
      if (member.hasRole("frontend")) {
        println(s"Backend: ready to send register from ${member.address}")
        context.actorSelection(RootActorPath(member.address) / "user" / "frontend") ! BackendRegistration
      }
  }
}

object Backend {
  def initiate(port: Int) = {
    val config = ConfigFactory.parseString(s"akka.remote.netty.tcp.port=$port")
      .withFallback(ConfigFactory.load("simple-cluster").getConfig("Backend"))

    val system = ActorSystem("ClusterSystem", config)

    val Backend = system.actorOf(Props[Backend], name = "Backend")
  }
}
