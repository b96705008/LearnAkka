package com.github.b96705008.akka.ch9_patterns.ordered_termination.pattern

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, Props, Terminated}
import akka.pattern.{gracefulStop, pipe}

import scala.concurrent.Future
import scala.concurrent.duration._

object Terminator {
  // Protocol between us and the master
  case class GetChildren(forActor: ActorRef)
  case class Children(kids: Iterable[ActorRef])
}

class Terminator(childProps: Props, numOfChildren: Int) extends Actor{
  import Terminator._
  import context._

  implicit val stopTimeout = 5.minutes
  case object AllDead

  override def preStart(): Unit = {
    (1 to numOfChildren).foreach { i =>
      context.actorOf(childProps, s"worker-$i")
    }
  }

  def order(kids: Iterable[ActorRef]): Iterable[ActorRef] = {
    kids.toSeq.reverse
  }

  def killKids(kids: List[ActorRef]): Future[Any] = {
    kids match {
      case kid :: Nil =>
        gracefulStop(kid, stopTimeout).flatMap {_ =>
          Future { AllDead }
        }
      case Nil =>
        Future { AllDead }
      case kid :: rest =>
        gracefulStop(kid, stopTimeout).flatMap { _ =>
          killKids(rest)
        }
    }
  }

  def waiting: Receive = {
    case GetChildren(forActor) =>
      watch(forActor)
      forActor ! Children(children)
      become(childrenGiven(forActor))
  }

  def childrenGiven(to: ActorRef): Receive = {
    case GetChildren(forActor) if sender == to =>
      forActor ! Children(children)
    case Terminated(`to`) =>
      killKids(order(children).toList) pipeTo self
    case AllDead =>
      stop(self)
      context.system.terminate()
  }

  override def receive: Receive = waiting
}
