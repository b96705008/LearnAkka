package com.github.b96705008.akka.ch8_http.restapi

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.github.b96705008.akka.ch8_http.restapi.models._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import spray.json._
import DefaultJsonProtocol._
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import com.github.b96705008.akka.ch8_http.restapi.db.TweetManager

import scala.concurrent.ExecutionContext
import scala.io.StdIn

trait RestApi {
  import TweetProtocol._
  import TweetEntity._
  import TweetEntityProtocol.entityFormat

  implicit val system: ActorSystem

  implicit val materializer: ActorMaterializer

  implicit val ec: ExecutionContext

  val route =
    pathPrefix("tweets") {
      (post & entity(as[Tweet])) { tweet =>
        complete {
          TweetManager.save(tweet) map { r =>
            Created -> Map("id" -> r.id).toJson
          }
        }
      } ~
      (get & path(Segment)) { id =>
        complete {
          TweetManager.findById(id) map { t =>
            OK -> t
          }
        }
      } ~
      (delete & path(Segment)) { id =>
        complete {
          TweetManager.deleteById(id) map { _ =>
            NoContent
          }
        }
      } ~
      (get) {
        complete {
          TweetManager.find map { ts =>
            OK -> ts.map(_.as[TweetEntity])
          }
        }
      }
    }
}

object Api extends App with RestApi {
  override implicit val system: ActorSystem = ActorSystem("rest-api")
  override implicit val materializer: ActorMaterializer = ActorMaterializer()
  override implicit val ec: ExecutionContext = system.dispatcher

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress Return to stop...")
  StdIn.readLine()

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
