package com.github.b96705008.akka.ch5_cluster.sharding

import akka.actor.{ActorIdentity, ActorPath, ActorSystem, Identify, Props}
import akka.cluster.Cluster
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.persistence.journal.leveldb.{SharedLeveldbJournal, SharedLeveldbStore}
import akka.util.Timeout
import akka.pattern.ask
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._

object ShardingApp extends App {

  startup(Seq("2551", "2552", "0"))

  def startup(ports: Seq[String]): Unit = {

    ports.foreach { port =>
      val config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port)
        .withFallback(ConfigFactory.load("sharding"))

      val system = ActorSystem("ClusterSystem", config)

      startupSharedJournal(system, startStore = port == "2551", path =
        ActorPath.fromString("akka.tcp://ClusterSystem@127.0.0.1:2551/user/store"))


      ClusterSharding(system).start(
        typeName = Counter.shardName,
        entityProps = Counter.props(),
        settings = ClusterShardingSettings(system),
        messageExtractor = Counter.messageExtractor
      )

      if (port != "2551" && port != "2552") {
        Cluster(system) registerOnMemberUp {
          system.actorOf(Props[Frontend], "frontend")
        }
      }
    }

    def startupSharedJournal(system: ActorSystem, startStore: Boolean, path: ActorPath): Unit = {
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
          system.log.error("Shared journal not started at {}", path)
          system.terminate()
      }
    }
  }
}
