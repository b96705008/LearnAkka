package com.github.b96705008.akka.ch5_cluster.singleton

import scala.concurrent.duration._
import akka.actor.{ActorIdentity, ActorPath, ActorSystem, Identify, PoisonPill, Props}
import akka.cluster.Cluster
import akka.cluster.singleton.{ClusterSingletonManager, ClusterSingletonManagerSettings}
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import akka.pattern.ask


object SingletonApp extends App {

  startup(Seq("2551", "2552", "0"))

  def startup(ports: Seq[String]): Unit = {
    ports foreach {port =>
      val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)
        .withFallback(ConfigFactory.load("singleton"))

      val system = ActorSystem("ClusterSystem", config)

      startupSharedJournal(system, startStore = port == "2551", path =
        ActorPath.fromString("akka.tcp://ClusterSystem@127.0.0.1:2551/user/store"))

      // ****
      val master = system.actorOf(ClusterSingletonManager.props(
        singletonProps = Props[Master],
        terminationMessage = PoisonPill,
        settings = ClusterSingletonManagerSettings(system).withRole(None)
      ), name = "master")

      Cluster(system) registerOnMemberUp {
        system.actorOf(Worker.props, name = "worker")
      }

      if (port != "2551" && port != "2552") {
        Cluster(system) registerOnMemberUp {
          system.actorOf(Frontend.props, name = "frontend")
        }
      }
    }

    def startupSharedJournal(system: ActorSystem, startStore: Boolean, path: ActorPath): Unit = {
      // start store by actor of SharedLeveldbStore
      if (startStore) system.actorOf(Props[SharedLeveldbStore], "store")

      import system.dispatcher
      implicit val timeout = Timeout(15.seconds)
      val f = system.actorSelection(path) ? Identify(None)

      f.onSuccess {
        case ActorIdentity(_, Some(ref)) =>
          SharedLeveldbJournal.setStore(ref, system)
        case _ =>
          system.log.error("Shared journal not started at {}", path)
          system.terminate()
      }

      f.onFailure {
        case _ =>
          system.log.error("Lookup of shared journal at {} time out", path)
          system.terminate()
      }
    }
  }
}
