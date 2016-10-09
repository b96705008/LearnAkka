package com.github.b96705008.akka.ch7_streams.twitter

import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by roger19890107 on 9/26/16.
  */
object StreamTweets extends App {
  implicit val actorSystem = ActorSystem()
  import actorSystem.dispatcher
  implicit val flowMaterializer = ActorMaterializer()

  val streamClient = new TwitterStreamClient(actorSystem)
  streamClient.init

  val source = Source.actorPublisher[Tweet](Props[StatusPublisher])

  val normalize = Flow[Tweet].filter { t =>
    t.hashTags.contains(HashTag("#Akka"))
    //true
  }

  val sink = Sink.foreach[Tweet](println)

  source.via(normalize).runWith(sink).andThen {
    case _ =>
      Await.ready(actorSystem.whenTerminated, 1 minute)
  }
}
