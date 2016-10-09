package com.github.b96705008.akka.ch6_testing.fsm

import akka.actor.{ActorSystem, FSM, Props, Stash}


object UserStorageFSM {
  // FSM State
  sealed trait State
  case object Connected extends State
  case object Disconnected extends State

  // FSM Data
  sealed trait Data
  case object EmptyData extends Data

  // Model
  trait DBOperation
  object DBOperation {
    case object Create extends DBOperation
    case object Update extends DBOperation
    case object Read extends DBOperation
    case object Delete extends DBOperation
  }

  case class User(username: String, email: String)

  // FSM Event
  case object Connect
  case object Disconnect
  case class Operation(op: DBOperation, user: User)
}

class UserStorageFSM extends FSM[UserStorageFSM.State, UserStorageFSM.Data] with Stash {
  import UserStorageFSM._

  startWith(Disconnected, EmptyData)

  when(Disconnected) {
    case Event(Connect, _) =>
      println("UserStorage connected to DB")
      unstashAll()
      goto(Connected) using EmptyData
    case Event(msg, _) =>
      stash()
      stay using EmptyData
  }

  when(Connected) {
    case Event(Disconnect, _) =>
      println("UserStorage disconnected from DB")
      goto(Disconnected) using EmptyData
    case Event(Operation(op, user), _) =>
      println(s"UserStorage receive $op operation to do in user: $user")
      stay using EmptyData
  }

  initialize()
}

object FiniteStateMachine extends App {
  import UserStorageFSM._

  val system = ActorSystem("Hotswap-FSM")

  val userStorage = system.actorOf(Props[UserStorageFSM], "userStorage-fsm")

  userStorage ! Connect

  userStorage ! Operation(DBOperation.Create, User("Admin", "admin@xxx.com"))

  userStorage ! Disconnect

  Thread.sleep(1000)

  system.terminate()
}