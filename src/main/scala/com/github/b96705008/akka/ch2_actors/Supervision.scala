package com.github.b96705008.akka.ch2_actors

import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props}
import akka.actor.Actor.Receive
import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume, Stop}

import scala.concurrent.duration._

class Aphrodite extends Actor {
  import Aphrodite._

  override def preStart(): Unit = {
    println("Aphrodite preStart hook ...")
  }

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    println("Aphrodite preRestart hook ...")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable): Unit = {
    println("Aphrodite postRestart hook ...")
    super.postRestart(reason)
  }

  override def postStop(): Unit = {
    println("Aphrodite postStop hook...")
  }

  override def receive: Receive = {
    case "Resume" => throw ResumeException
    case "Stop" => throw StopException
    case "Restart" => throw RestartException
    case _ => throw new Exception
  }
}

object Aphrodite {
  case object ResumeException extends Exception
  case object StopException extends Exception
  case object RestartException extends Exception
}

class Hera extends Actor {
  import Aphrodite._

  var childRef: ActorRef = _

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 seconds) {
      case ResumeException => Resume
      case StopException => Stop
      case RestartException => Restart
      case _: Exception => Escalate
    }

  override def preStart(): Unit = {
    childRef = context.actorOf(Props[Aphrodite], "Aphrodite")
    Thread.sleep(100)
  }

  override def receive: Receive = {
    case msg =>
      println(s"Hera received ${msg}")
      childRef ! msg
      Thread.sleep(100)
  }
}

object Supervision extends App {
  val system = ActorSystem("supervision")

  val hera = system.actorOf(Props[Hera], "hera")

  //hera ! "Resume"
  //hera ! "Restart"
  hera ! "Stop"
  Thread.sleep(100)
  println()

  system.terminate()
}
