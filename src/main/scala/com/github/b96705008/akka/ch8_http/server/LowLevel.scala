package com.github.b96705008.akka.ch8_http.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpEntity, HttpRequest, HttpResponse, Uri}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model.HttpMethods._
import akka.stream.scaladsl.Sink

import scala.concurrent.Future
import scala.io.StdIn

/**
  * Created by roger19890107 on 9/28/16.
  */
object LowLevel extends App {
  implicit val system = ActorSystem()

  implicit val materializer = ActorMaterializer()

  implicit val ec = system.dispatcher

  val serverSource = Http().bind(interface = "localhost", port = 8888)

  val requestHandler: HttpRequest => HttpResponse = {
    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      HttpResponse(entity = HttpEntity("Hello Akka HTTP Server Side API - Low Level"))
    case _ =>
      HttpResponse(404, entity = "Unknown resource!")
  }

  val bindingFuture: Future[Http.ServerBinding] = serverSource.to(Sink.foreach { connection =>
    println("Accepted new connection from " + connection.remoteAddress)
    connection handleWithSyncHandler requestHandler
  }).run()

  println(s"Server online at http://localhost:8888/\nPress Return to stop...")
  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
