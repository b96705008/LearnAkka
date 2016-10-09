package com.github.b96705008.akka.ch3_tools.paths

import akka.actor.Actor


class Counter extends Actor {
  import Counter._

  var counter = 0

  def receive = {
    case Inc(x) =>
      counter += x
    case Dec(x) =>
      counter -= x
  }
}

object Counter {
  final case class Inc(num: Int)
  final case class Dec(num: Int)
}
