package com.github.b96705008.akka.ch5_cluster.load_balancing

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.actor.Actor.Receive
import akka.cluster.Cluster
import akka.routing.FromConfig
import com.github.b96705008.akka.ch5_cluster.commons.Add
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import scala.util.Random

/**
  * Created by roger19890107 on 9/18/16.
  */
class Frontend extends Actor {
  import context.dispatcher

  val backend = context.actorOf(FromConfig.props(), name = "backendRouter")

  context.system.scheduler.schedule(3 seconds, 3 seconds, self,
    Add(Random.nextInt(100), Random.nextInt(100)))

  override def receive: Receive = {
    case addOp: Add =>
      println("Frontend: I'll forward add operation to backend node to handle it.")
      backend forward addOp
  }
}

object Frontend {
  private var _frontend: ActorRef = _

  val upToN = 200

  def initiate() = {
    val config = ConfigFactory.parseString("akka.simple-cluster.conf.roles = [frontend]")
      .withFallback(ConfigFactory.load("loadbalancer"))

    val system = ActorSystem("ClusterSystem", config)
    system.log.info("Frontend will start when 2 backend members in the simple-cluster.conf.")

    Cluster(system) registerOnMemberUp {
      _frontend = system.actorOf(Props[Frontend], name = "frontend")
    }
  }

  def getFrontend = _frontend
}