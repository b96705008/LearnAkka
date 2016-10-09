package com.github.b96705008.akka.ch7_streams.testing

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by roger19890107 on 9/27/16.
  */
class StreamKitSpec extends TestKit(ActorSystem("test-system"))
  with FlatSpecLike
  with ImplicitSender
  with BeforeAndAfterAll
  with MustMatchers {

  import system.dispatcher

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  implicit val flowMaterializer = ActorMaterializer()

//  "With Stream Test Kit" should "use a TestSink to test a source" in {
//    val sourceUnderTest = Source(1 to 4).filter(_ < 3).map(_ * 2)
//
//    sourceUnderTest
//      .runWith(TestSink.probe[Int](system))
//      .request(2)
//      .expectNext(2, 4)
//      .expectComplete()
//  }

  it should "use a TestSource to test a sink" in {
    val sinkUnderTest = Sink.cancelled

    TestSource.probe[Int]
      .toMat(sinkUnderTest)(Keep.left)
      .run()
      .expectCancellation()
  }
}
