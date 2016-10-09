package com.github.b96705008.akka.ch6_testing.relationship

import akka.actor.{ActorRefFactory, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}

/**
  * Created by roger19890107 on 9/25/16.
  */
class ParentSpec extends TestKit(ActorSystem("test-system"))
  with ImplicitSender
  with FlatSpecLike
  with BeforeAndAfterAll
  with MustMatchers {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "Parent" should "send ping message to child when receive ping it message" in {
    val child = TestProbe()

    val childMaker = (_: ActorRefFactory) => child.ref

    val parent = system.actorOf(Props(new Parent(childMaker)))

    parent ! "ping"

    child.expectMsg("ping")
  }

}
