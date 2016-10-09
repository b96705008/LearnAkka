package com.github.b96705008.akka.ch7_streams.graph

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, ClosedShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}

import scala.concurrent.Await
import scala.concurrent.duration._

object GraphFlow extends App {
  implicit val actorSystem = ActorSystem()
  import actorSystem.dispatcher
  implicit val flowMaterializer = ActorMaterializer()

  val in = Source(1 to 10)

  val out = Sink.foreach[Int](println)

  val f1, f3 = Flow[Int].map(_ + 10)

  val f2 = Flow[Int].map(_ * 5)

  val f4 = Flow[Int].map(_ + 0)

  val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
    import GraphDSL.Implicits._

    val bcast = builder.add(Broadcast[Int](2))
    val merge = builder.add(Merge[Int](2))

    in ~> f1 ~> bcast ~> f2 ~> merge ~> f3 ~> out
                bcast ~> f4 ~> merge

    ClosedShape
  })

  g.run()

  val topHeadSink = Sink.head[Int]
  val bottomHeadSink = Sink.head[Int]
  val shareDoubler = Flow[Int].map(_ * 2)

  val g2 = RunnableGraph.fromGraph(GraphDSL.create(topHeadSink, bottomHeadSink)((_, _)) {
    implicit builder => (topHS, bottomHS) =>
      import GraphDSL.Implicits._

      val bcast = builder.add(Broadcast[Int](2))
      Source.single(1) ~> bcast.in

      bcast.out(0) ~> shareDoubler ~> topHS.in
      bcast.out(1) ~> shareDoubler ~> bottomHS.in
      ClosedShape
  })

  val (topResult, bottomResult) = g2.run()
  topResult.mapTo[Int].collect {
    case x: Int => println(x)
    case _ => println("failure")
  }
}
