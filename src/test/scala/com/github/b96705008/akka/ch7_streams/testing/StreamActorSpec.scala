package com.github.b96705008.akka.ch7_streams.testing

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}
import akka.pattern.pipe

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by roger19890107 on 9/27/16.
  */
class StreamActorSpec extends TestKit(ActorSystem("test-system"))
  with FlatSpecLike
  with ImplicitSender
  with BeforeAndAfterAll
  with MustMatchers {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  import system.dispatcher
  implicit val flowMaterializer = ActorMaterializer()


  "With TestKit" should "test actor receive elements from the sink" in {
    val source = Source(1 to 4).grouped(2)

    val testProbe = TestProbe()

    source.grouped(2).runWith(Sink.head) pipeTo testProbe.ref

    testProbe.expectMsg(100.millis, Seq(Seq(1, 2), Seq(3, 4)))
  }

  it should "have a control over a receiving elements" in  {
    case object Tick

    val source = Source.tick(0.millis, 200.millis, Tick)

    val probe = TestProbe()

    val sink = Sink.actorRef(probe.ref, "completed")

    val runnable = source.to(sink).run()

    probe.expectMsg(1.second, Tick)
    probe.expectNoMsg(100.millis)
    probe.expectMsg(200.millis, Tick)
    runnable.cancel()
    probe.expectMsg(200.millis, "completed")
  }

  it should "have a control over elements to be sent" in {
    val sink = Flow[Int].map(_.toString).toMat(Sink.fold("")(_ + _))(Keep.right)

    val source = Source.actorRef(8, overflowStrategy = OverflowStrategy.fail)

    val (ref, result) = source.toMat(sink)(Keep.both).run()

    ref ! 1
    ref ! 2
    ref ! 3
    ref ! akka.actor.Status.Success("done")

    Await.result(result, 200.millis) must equal("123")
  }
}
