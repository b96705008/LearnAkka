package com.github.b96705008.akka.ch7_streams.twitter

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import twitter4j.Status

/**
  * Created by roger19890107 on 9/25/16.
  */
object ReactiveTweets extends App {
  implicit val actorSystem = ActorSystem()
  import actorSystem.dispatcher
  implicit val flowMaterializer = ActorMaterializer()

  val source = Source.fromIterator(() => TwitterClient.retrieveTweets("#Akka"))

  val normalize = Flow[Status].map { t =>
    Tweet(Author(t.getUser().getName), t.getText)
  }

  val sink = Sink.foreach[Tweet](println)

  source.via(normalize).runWith(sink).andThen {
    case _ =>
      actorSystem.terminate()
  }
}
