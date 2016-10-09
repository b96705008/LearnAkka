package com.github.b96705008.akka.ch3_tools.behavior

import akka.actor.{Actor, ActorSystem, FSM, Props, Stash}

object UserStorageFSM {

  // FSM State
  sealed trait State
  case object Connected extends State
  case object Disconnected extends State

  // FSM Data
  sealed trait Data
  case object EmptyData extends Data

  trait DBOperation
  object DBOperation {
    case object Create extends DBOperation
    case object Update extends DBOperation
    case object Read extends DBOperation
    case object Delete extends DBOperation
  }

  // Event
  case object Connect
  case object Disconnect
  case class Operation(dBOperation: DBOperation, user: Option[User])

  case class User(username: String, email: String)
}

class UserStorageFSM extends FSM[UserStorageFSM.State, UserStorageFSM.Data] with Stash {
  import UserStorageFSM._

  // 1. define start with
  startWith(Disconnected, EmptyData)

  // 2. define states
  when(Disconnected) {
    case Event(Connect, _) =>
      println("User Storage connected to DB")
      unstashAll()
      goto(Connected) using EmptyData

    case Event(_, _) =>
      stash()
      stay using EmptyData
  }

  when(Connected) {
    case Event(Disconnect, _) =>
      println("User Storage Disconnect from DB")
      goto(Disconnected) using EmptyData

    case Event(Operation(op, user), _) =>
      println(s"User Storage receive ${op} to do in user: ${user}")
      stay using EmptyData
  }

  // 3. initialize
  initialize()
}

object FiniteStateMachine extends App {
  import UserStorageFSM._

  val system = ActorSystem("Hotswap-FSM")

  val userStorage = system.actorOf(Props[UserStorageFSM], "userStorage-fsm")

  userStorage ! Operation(DBOperation.Create, Some(User("Admin", "admin@aa.com")))
  userStorage ! Operation(DBOperation.Update, Some(User("Admin", "admin@aa.com")))

  userStorage ! Connect

  userStorage ! Operation(DBOperation.Read, Some(User("Admin", "admin@aa.com")))

  userStorage ! Disconnect

  Thread.sleep(100)

  system.terminate()
}
