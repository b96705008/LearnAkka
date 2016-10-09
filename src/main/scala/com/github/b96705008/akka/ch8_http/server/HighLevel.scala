package com.github.b96705008.akka.ch8_http.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives._

import scala.io.StdIn

/**
  * Created by roger19890107 on 9/28/16.
  */
object HighLevel extends App {
  implicit val system = ActorSystem()

  implicit val materializer = ActorMaterializer()

  implicit val ec = system.dispatcher

  val route =
    path("") {
      get {
        complete("Hello Akka HTTP Server Side API - High Level")
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8888/\nPress Return to stop...")
  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
