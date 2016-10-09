package com.github.b96705008.akka.ch8_http.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * Created by roger19890107 on 9/28/16.
  */
object HostLevel extends App {
  import JsonProtocol._

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  val poolClientFlow = Http().cachedHostConnectionPool[Int]("api.ipify.org")

  val responseFuture: Future[(Try[HttpResponse], Int)] =
    Source.single(HttpRequest(uri = "/?format=json") -> 4)
      .via(poolClientFlow)
      .runWith(Sink.head)

  responseFuture map {
    case (Success(res), i) =>
      res.status match {
        case OK =>
          Unmarshal(res.entity).to[IpInfo].map { info =>
            println(s"The information for my ip is: $info, and pool number is $i")
            shutdown()
          }
        case _ =>
          Unmarshal(res.entity).to[String].map { body =>
            println(s"Ths response status is ${res.status} and response body is $body")
            shutdown()
          }
      }
    case (Failure(err), i) =>
      println(s"Error happened $err, and pool number is $i")
      shutdown()
  }

  def shutdown() = {
    Http().shutdownAllConnectionPools().onComplete { _ =>
      system.terminate()
    }
  }
}
