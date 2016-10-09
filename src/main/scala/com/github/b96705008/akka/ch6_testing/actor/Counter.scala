package com.github.b96705008.akka.ch6_testing.actor

import akka.actor.Actor
import akka.actor.Actor.Receive


class Counter extends Actor {
  import Counter._

  var count: Int = 0

  override def receive: Receive = {
    case Increment =>
      count += 1
    case Decrement =>
      count -= 1
    case GetCount =>
      sender() ! count
  }
}

object Counter {
  case object Increment
  case object Decrement
  case object GetCount
}
