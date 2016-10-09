package com.github.b96705008.akka.ch7_streams.streamio

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.util.{Success, Failure}

object WriteStream extends App {
  implicit val actorSystem = ActorSystem()
  import actorSystem.dispatcher
  implicit val flowMaterializer = ActorMaterializer()

  // Source
  val source = Source(1 to 10000).filter(isPrime)

  // Sink
  val sink = FileIO.toPath(Paths.get("target/prime.txt"))

  val fileSink = Flow[Int]
    .map(i => ByteString(i.toString + "\n"))
    .toMat(sink)((_, bytesWritten) => bytesWritten)

  val consoleSink = Sink.foreach[Int](println)

  // graph
  val g = RunnableGraph.fromGraph(GraphDSL.create(fileSink, consoleSink)((file, _) => file) {
    implicit builder => (file, console) =>
      import GraphDSL.Implicits._

      val bcast = builder.add(Broadcast[Int](2))

      source ~> bcast ~> file
                bcast ~> console

      ClosedShape
  }).run()

  g.onComplete {
    case Success(_) =>
      actorSystem.terminate()
    case Failure(e) =>
      println(s"Failure: ${e.getMessage}")
      actorSystem.terminate()
  }

  def isPrime(n: Int): Boolean = {
    if (n <= 1) false
    else if (n == 2) true
    else !(2 until n).exists(x => n % x == 0)
  }
}
