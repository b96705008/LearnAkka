package com.github.b96705008.akka.ch6_testing.relationship

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}

/**
  * Created by roger19890107 on 9/25/16.
  */
class ChildSpec extends TestKit(ActorSystem("test-system"))
                with ImplicitSender
                with FlatSpecLike
                with BeforeAndAfterAll
                with MustMatchers {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Child Actor" should "send pong message when receive ping message" in {
    val parent = TestProbe()

    val child = system.actorOf(Props(new Child(parent.ref)))

    child ! "ping"

    parent.expectMsg("pong")
  }

}
