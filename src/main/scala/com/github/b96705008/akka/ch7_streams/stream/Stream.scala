package com.github.b96705008.akka.ch7_streams.stream

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by roger19890107 on 9/25/16.
  */
object Stream extends App {
  implicit val actorSystem = ActorSystem()
  import actorSystem.dispatcher
  implicit val flowMaterializer = ActorMaterializer()

  // Source
  val input = Source(1 to 100)

  // Flow
  val normalize = Flow[Int].map(_ * 2)

  // Sink
  val output = Sink.foreach[Int](println)

  input.via(normalize).runWith(output).andThen {
    case _ =>
      actorSystem.terminate()
      //Await.ready(actorSystem.whenTerminated, 1 minute)
  }
}
