package com.github.b96705008.akka.ch7_streams.testing

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by roger19890107 on 9/26/16.
  */
class SimpleStreamSpec extends TestKit(ActorSystem("test-system"))
  with FlatSpecLike
  with ImplicitSender
  with BeforeAndAfterAll
  with MustMatchers {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import system.dispatcher
  implicit val flowMaterializer = ActorMaterializer()

  "Simple Sink" should "return the correct results" in {
    val sinkUnderTest = Sink.fold[Int, Int](0)(_ + _)

    val source = Source(1 to 4)

    val result = source.runWith(sinkUnderTest)

    Await.result(result, 100.millis) must equal(10)
  }

  "Simple Source" should "contains a correct elements" in {
    val source = Source(1 to 10)

    val result = source.grouped(2).runWith(Sink.head)

    Await.result(result, 100.millis) must equal(1 to 2)
  }

  "Simple Flow" should "do right transformation" in {
    val flow = Flow[Int].takeWhile(_ < 5)

    val result = Source(1 to 10).via(flow).runWith(Sink.fold(Seq.empty[Int])(_ :+ _))

    Await.result(result, 100.millis) must equal(1 to 4)
  }
}
