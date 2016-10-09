package com.github.b96705008.akka.ch5_cluster.singleton

import akka.actor.Actor.Receive

import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}

/**
  * Created by roger19890107 on 9/19/16.
  */
class Worker extends Actor with ActorLogging {
  import Master._
  import context.dispatcher

  val masterProxy = context.actorOf(ClusterSingletonProxy.props(
    singletonManagerPath = "/user/master",
    settings = ClusterSingletonProxySettings(context.system).withRole(None)
  ), name = "masterProxy")

  context.system.scheduler.schedule(0.seconds, 30.seconds, masterProxy, RegisterWorker(self))
  context.system.scheduler.schedule(3.seconds, 3.seconds, masterProxy, RequestWork(self))

  override def receive: Receive = {
    case Work(requester, op) =>
      log.info(s"Worker: I received work with op: $op and I will reply to $requester")
  }
}

object Worker {
  def props = Props(new Worker())
}