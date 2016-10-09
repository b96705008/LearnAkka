package com.github.b96705008.akka.ch5_cluster.load_balancing

/**
  * Created by roger19890107 on 9/18/16.
  */
object LoadBalancingApp extends App {
  Backend.initiate(2571)

  Backend.initiate(2572)

  Backend.initiate(2573)

  Frontend.initiate()
}
