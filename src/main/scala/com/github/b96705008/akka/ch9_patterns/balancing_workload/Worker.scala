package com.github.b96705008.akka.ch9_patterns.balancing_workload

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorLogging, ActorPath, ActorRef}


abstract class Worker(masterLocation: ActorPath) extends Actor with ActorLogging {
  import MasterWorkerProtocol._

  val master = context.actorSelection(masterLocation)

  case class WorkComplete(result: Any)

  def doWork(workSender: ActorRef, work: Any): Unit

  override def preStart(): Unit = master ! WorkerCreated(self)

  def working(work: Any): Receive = {
    case WorkIsReady =>
      // working..., do not bother

    case NoWorkToBeDone =>
      // working..., I do not ask for work

    case WorkToBeDone(_) =>
      log.error("Master does not know that I have work?")

    case WorkComplete(result) =>
      log.info("Work is complete. Result {}.", result)
  }

  def idle: Receive = {
    case WorkIsReady =>
      log.info("Requesting Work")
      master ! WorkerRequestsWork(self)

    case WorkToBeDone(work) =>
      log.info("Got work {}", work)
      doWork(sender, work)
      context.become(working(work))

    case NoWorkToBeDone =>
      log.info("The master does not have the any work now.")
  }

  override def receive: Receive = idle
}
