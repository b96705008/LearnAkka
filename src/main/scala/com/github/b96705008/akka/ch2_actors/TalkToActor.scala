package com.github.b96705008.akka.ch2_actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import Recorder._
import Checker._
import Storage._
import akka.util.Timeout
import akka.pattern.ask

case class User(username: String, email: String)

object Recorder {
  sealed trait RecordMsg
  case class NewUser(user: User) extends RecordMsg

  def props(checker: ActorRef, storage: ActorRef) = Props(new Recorder(checker, storage))
}

object Checker {
  sealed trait CheckerMsg
  case class CheckUser(user: User) extends CheckerMsg

  sealed trait CheckerResponse
  case class BlackUser(user: User) extends CheckerResponse
  case class WhiteUser(user: User) extends CheckerResponse
}

object Storage {
  sealed trait StorageMsg
  case class AddUser(user: User) extends StorageMsg
}

class Storage extends Actor {
  var users = List.empty[User]

  override def receive: Receive = {
    case AddUser(user) =>
      println(s"Storage: $user added")
      users = user :: users
  }
}

class Checker extends Actor {
  val blackList = List(
    User("Adam", "adam@mail.com")
  )

  override def receive: Receive = {
    case CheckUser(user) if blackList.contains(user) =>
      println(s"Checker: $user in the blacklist")
      sender() ! BlackUser(user)
    case CheckUser(user) =>
      println(s"Checker: $user not in the blacklist")
      sender() ! WhiteUser(user)
  }
}

class Recorder(checker: ActorRef, storage: ActorRef) extends Actor {
  //http://stackoverflow.com/questions/22702844/what-is-the-behavior-of-scala-concurrent-executioncontext-implicits-global
  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  implicit val timeout = Timeout(5 seconds)

  override def receive: Receive = {
    case NewUser(user) =>
      checker ? CheckUser(user) map {
        case WhiteUser(user) =>
          storage ! AddUser(user)
        case BlackUser(user) =>
          println(s"Recorder: $user in the blacklist")
      }
  }
}

object TalkToActor extends App {
  val system = ActorSystem("talk-to-actor")

  val checker = system.actorOf(Props[Checker], "checker")

  val storage = system.actorOf(Props[Storage], "storage")

  val recorder = system.actorOf(Recorder.props(checker, storage), "recorder")

  recorder ! Recorder.NewUser(User("Jon", "jon@packt.com"))

  Thread.sleep(100)

  system.terminate()
}