package com.github.b96705008.akka.ch5_cluster.cluster

import com.github.b96705008.akka.ch5_cluster.commons.Add

/**
  * Created by roger19890107 on 9/18/16.
  */
object ClusterApp extends App {

  Frontend.initiate()

  Backend.initiate(2552)

  Backend.initiate(2560)

  Backend.initiate(2561)

  Thread.sleep(10000)

  Frontend.getFrontend ! Add(2, 4)
}
