package com.github.b96705008.akka.ch8_http.restapi.db

import com.typesafe.config.ConfigFactory
import reactivemongo.api.MongoDriver

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by roger19890107 on 9/29/16.
  */
object MongoDB {
  val config = ConfigFactory.load("restapi")
  val database = config.getString("mongodb.database")
  val servers = config.getStringList("mongodb.servers").asScala

  val driver = new MongoDriver()
  val connection = driver.connection(servers)

  val db = connection(database)
}
