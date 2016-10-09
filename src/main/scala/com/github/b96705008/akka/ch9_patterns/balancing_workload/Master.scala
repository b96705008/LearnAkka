package com.github.b96705008.akka.ch9_patterns.balancing_workload

import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}

/**
  * Created by roger19890107 on 10/4/16.
  */

object MasterWorkerProtocol {
  // Messages from Workers
  case class WorkerCreated(worker: ActorRef)
  case class WorkerRequestsWork(worker: ActorRef)
  case class WorkIsDone(worker: ActorRef)

  // Messages to Workers
  case class WorkToBeDone(work: Any)
  case object WorkIsReady
  case object NoWorkToBeDone
}

class Master extends Actor with ActorLogging {
  import MasterWorkerProtocol._
  import scala.collection.mutable

  val workers = mutable.Map.empty[ActorRef, Option[(ActorRef, Any)]]

  val workQ = mutable.Queue.empty[(ActorRef, Any)]

  def notifyWorkers(): Unit = {
    if (workQ.isEmpty) {
      workers.foreach {
        case (worker, m) if m.isEmpty => worker ! WorkIsReady
        case _ =>
      }
    }
  }

  override def receive: Receive = {
    case WorkerCreated(worker) =>
      log.info("Worker created: {}", worker)
      context.watch(worker)
      workers += (worker -> None)
      notifyWorkers()

    case WorkerRequestsWork(worker) =>
      log.info("Worker requests work: {}", worker)
      if (workers.contains(worker)) {
        if (workQ.isEmpty) {
          worker ! NoWorkToBeDone
        } else if (workers(worker).isEmpty) {
          val (workSender, work) = workQ.dequeue()
          workers += (worker -> Some(workSender, work))
          worker.tell(WorkToBeDone(work), workSender)
        }
      }

    case WorkIsDone(worker) =>
      if (!workers.contains(worker)) {
        log.error("Blurgh! {} said it's done work but we didn't know about him", worker)
      } else {
        workers += (worker -> None)
      }

    case Terminated(worker) =>
      if (workers.contains(worker) && workers(worker).isDefined) {
        log.error("Blurgh! {} died while processing {}", worker, workers(worker))
        val (workSender , work) = workers(worker).get
        // reassign work to me for dispatch
        self.tell(work, workSender)
      }

    case work =>
      log.info("Queueing {}", work)
      workQ.enqueue(sender() -> work)
      notifyWorkers()
  }
}
