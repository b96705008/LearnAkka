package com.github.b96705008.akka.ch7_streams.streamio

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Framing, Sink}
import akka.util.ByteString


object ReadStream extends App {
  implicit val actorSystem = ActorSystem()
  import actorSystem.dispatcher
  implicit val flowMaterializer = ActorMaterializer()

  val logFile = Paths.get("target/log.txt")

  val source = FileIO.fromPath(logFile)

  val flow = Framing.delimiter(
    ByteString("\n"),
    maximumFrameLength = 256,
    allowTruncation = true).map(_.utf8String)

  val sink = Sink.foreach(println)

  source.via(flow).runWith(sink).andThen {
    case _ =>
      actorSystem.terminate()
  }
}
