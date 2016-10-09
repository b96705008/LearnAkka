package com.github.b96705008.akka.ch3_tools.behavior

import akka.actor.{Actor, ActorSystem, Props, Stash}


case class User(username: String, email: String)

object UserStorage {

  trait DBOperation
  object DBOperation {
    case object Create extends DBOperation
    case object Update extends DBOperation
    case object Read extends DBOperation
    case object Delete extends DBOperation
  }

  case object Connect
  case object Disconnect
  case class Operation(dBOperation: DBOperation, user: Option[User])
}

class UserStorage extends Actor with Stash {
  import UserStorage._

  def receive = disconnected

  def connected: Receive = {
    case Disconnect =>
      println("User Storage Disconnect from DB")
      context.unbecome()
    case Operation(op, user) =>
      println(s"User Storage receive ${op} to do in user: ${user}")
  }

  def disconnected: Receive = {
    case Connect =>
      println("User Storage connected to DB")
      unstashAll()
      context.become(connected)
    case _ =>
      stash()
  }
}

object BecomeHotswap extends App {
  import UserStorage._

  val system = ActorSystem("Hotswap-Become")

  val userStorage = system.actorOf(Props[UserStorage], "userStorage")

  userStorage ! Operation(DBOperation.Create, Some(User("Admin", "admin@aa.com")))
  userStorage ! Operation(DBOperation.Update, Some(User("Admin", "admin@aa.com")))

  userStorage ! Connect

  userStorage ! Operation(DBOperation.Read, Some(User("Admin", "admin@aa.com")))

  userStorage ! Disconnect

  Thread.sleep(100)

  system.terminate()
}