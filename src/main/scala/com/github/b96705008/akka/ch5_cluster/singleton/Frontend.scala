package com.github.b96705008.akka.ch5_cluster.singleton

import akka.actor.Actor.Receive

import scala.concurrent.duration._
import akka.actor.{Actor, ActorLogging, Props}
import akka.cluster.singleton.{ClusterSingletonProxy, ClusterSingletonProxySettings}

/**
  * Created by roger19890107 on 9/19/16.
  */
class Frontend extends Actor with ActorLogging{
  import Frontend._
  import context.dispatcher

  val masterProxy = context.actorOf(ClusterSingletonProxy.props(
    singletonManagerPath = "/user/master",
    settings = ClusterSingletonProxySettings(context.system).withRole(None)
  ), name = "masterProxy")

  context.system.scheduler.schedule(0.seconds, 3.seconds, self, Tick)

  override def receive: Receive = {
    case Tick =>
      masterProxy ! Master.Work(self, "add")
  }
}

object Frontend {
  case object Tick

  def props = Props(new Frontend())
}
