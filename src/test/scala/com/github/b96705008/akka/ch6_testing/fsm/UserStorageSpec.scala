package com.github.b96705008.akka.ch6_testing.fsm

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestFSMRef, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, MustMatchers}

/**
  * Created by roger19890107 on 9/25/16.
  */
class UserStorageSpec extends TestKit(ActorSystem("test-system"))
  with ImplicitSender
  with FlatSpecLike
  with BeforeAndAfterAll
  with MustMatchers {

  import UserStorageFSM._

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "User Storage" should "Start with disconnected state and empty data" in {
    val storage = TestFSMRef(new UserStorageFSM())

    storage.stateName must equal(Disconnected)
    storage.stateData must equal(EmptyData)
  }

  it should "be connected state if it receive a connect message" in {
    val storage = TestFSMRef(new UserStorageFSM())

    storage ! Connect

    storage.stateName must equal(Connected)
    storage.stateData must equal(EmptyData)
  }

  it should "be still in disconnected state if it receive any other message" in {
    val storage = TestFSMRef(new UserStorageFSM())

    //storage ! DBOperation.Create
    storage ! Operation(DBOperation.Create, User("Admin", "admin@xxx.com"))

    storage.stateName must equal(Disconnected)
    storage.stateData must equal(EmptyData)
  }

  it should "be switch to disconnected when it receive a disconnect message on Connected state" in {
    val storage = TestFSMRef(new UserStorageFSM())

    storage ! Connect

    storage ! Disconnect

    storage.stateName must equal(Disconnected)
    storage.stateData must equal(EmptyData)
  }

  it should "be still on connected state if it receive any DB operations" in {
    val storage = TestFSMRef(new UserStorageFSM())

    storage ! Connect
    storage ! Operation(DBOperation.Create, User("Admin", "admin@xxx.com"))

    storage.stateName must equal(Connected)
    storage.stateData must equal(EmptyData)

  }
}
