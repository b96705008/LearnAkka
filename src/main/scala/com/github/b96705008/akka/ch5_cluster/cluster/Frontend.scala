package com.github.b96705008.akka.ch5_cluster.cluster

import akka.actor.{Actor, ActorRef, ActorSystem, Props, Terminated}
import com.github.b96705008.akka.ch5_cluster.commons.{Add, BackendRegistration}
import com.typesafe.config.ConfigFactory

import scala.util.Random


class Frontend extends Actor {
  var backends = IndexedSeq.empty[ActorRef]

  override def receive: Receive = {
    case Add if backends.isEmpty =>
      println("Sevice unavailable, simple-cluster.conf does not have backend node.")
    case addOp: Add =>
      println("Frontend: I'll forward add operation to backend node to handle it.")
      backends(Random.nextInt(backends.size)) forward addOp
    case BackendRegistration if !backends.contains(sender()) =>
      backends = backends :+ sender()
      context watch sender()
      println(s"Frontend: I register backend ${sender().path}")
    case Terminated(a) =>
      backends = backends.filterNot(_ == a)
  }
}

object Frontend {
  private var _frontend: ActorRef = _

  def initiate() = {
    val config = ConfigFactory.load("simple-cluster").getConfig("Frontend")

    val system = ActorSystem("ClusterSystem", config)

    _frontend = system.actorOf(Props[Frontend], name = "frontend")
  }

  def getFrontend = _frontend
}
