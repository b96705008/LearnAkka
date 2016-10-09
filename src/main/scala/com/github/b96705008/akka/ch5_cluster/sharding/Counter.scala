package com.github.b96705008.akka.ch5_cluster.sharding

import akka.actor.{ActorLogging, Props}
import akka.cluster.sharding.ShardRegion.MessageExtractor
import akka.persistence.PersistentActor

import scala.concurrent.duration._

class Counter extends PersistentActor with ActorLogging {
  import Counter._

  context.setReceiveTimeout(120.seconds)

  var count = 0

  def updateState(event: CounterChanged): Unit = {
    count += event.delta
  }

  override def receiveRecover: Receive = {
    case evt: CounterChanged => updateState(evt)
  }

  override def receiveCommand: Receive = {
    case Increment =>
      log.info(s"Counter with path: $self received Increment Command")
      persist(CounterChanged(1))(updateState)
    case Decrement =>
      log.info(s"Counter with path: $self received Decrement Command")
      persist(CounterChanged(-1))(updateState)
    case Get =>
      log.info(s"Counter with path: $self received Get Command")
      log.info(s"Count = $count")
      sender() ! count
    case Stop =>
      context.stop(self)
  }

  override def persistenceId: String = context.parent.path.name + "-" + self.path.name
}

object Counter {
  trait Command
  case object Increment extends Command
  case object Decrement extends Command
  case object Get extends Command
  case object Stop extends Command

  trait Event
  case class CounterChanged(delta: Int) extends Event

  // Sharding Name
  val shardName: String = "Counter"

  // message from outside world
  case class CounterMessage(id: Long, cmd: Command)

  // messageExtractor
  val messageExtractor = new MessageExtractor {

    // id extractor
    override def entityId(message: Any): String = message match {
        case CounterMessage(id, _) => id.toString
        case _ => ""
    }

    // shard resolver
    override def shardId(message: Any): String = message match {
      case CounterMessage(id, _) => (id % 12).toString
      case _ => ""
    }

    // get message
    override def entityMessage(message: Any): Any = message match {
      case CounterMessage(_, msg) => msg
    }
  }

  def props() = Props[Counter]
}

