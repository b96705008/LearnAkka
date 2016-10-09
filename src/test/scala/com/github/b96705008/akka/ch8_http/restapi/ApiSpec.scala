package com.github.b96705008.akka.ch8_http.restapi

import akka.http.scaladsl.model.{MediaTypes, StatusCodes}
import MediaTypes._
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.github.b96705008.akka.ch8_http.restapi.db.{Created, TweetManager}
import com.github.b96705008.akka.ch8_http.restapi.models.{Tweet, TweetEntity, TweetEntityProtocol, TweetProtocol}
import org.scalatest.{BeforeAndAfterAll, FlatSpec, MustMatchers, WordSpec}
import reactivemongo.bson.BSONObjectID
import akka.http.scaladsl.server.Directives._
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.Await
import scala.concurrent.duration._

/**
  * Created by roger19890107 on 9/29/16.
  */
class ApiSpec extends FlatSpec
  with MustMatchers
  with ScalatestRouteTest
  with BeforeAndAfterAll
  with RestApi {

  import TweetProtocol._
  import TweetEntity._
  import TweetEntityProtocol.entityFormat

  override implicit val ec = system.dispatcher

  override def afterAll: Unit = {
    TweetManager.collection.drop()
  }

  "The Server" should "return Ok response when get all tweets" in {
    val tweet = Tweet("akka", "Hello World")
    val f = TweetManager.save(TweetEntity.toTweetEntity(tweet))
    val Created(id) = Await.result(f, 1.second)

    Get("/tweets") ~> route ~> check {
      status must equal(StatusCodes.OK)
      val res = responseAs[List[TweetEntity]]
      res.size must equal(1)
      res(0) must equal(TweetEntity(BSONObjectID(id), tweet.author, tweet.body))
    }
  }

  it should "return created response when create new tweet" in {
    Post("/tweets", Tweet("akka", "hello world")) ~> route ~> check {
      status must equal(StatusCodes.Created)
    }
  }

  it should "return No content response when delete a tweet" in {
    val tweet = Tweet("akka", "Hello world")
    val f = TweetManager.save(TweetEntity.toTweetEntity(tweet))
    val Created(id) = Await.result(f, 1.second)

    Delete(s"/tweets/$id") ~> route ~> check {
      status must equal(StatusCodes.NoContent)
    }
  }

  it should "return Ok response when get a tweet" in {
    val tweetEntity = TweetEntity.toTweetEntity(Tweet("akka", "Hello World"))
    val f = TweetManager.save(tweetEntity)
    val Created(id) = Await.result(f, 1.second)

    Get(s"/tweets/$id") ~> route ~> check {
      status must equal(StatusCodes.OK)
      responseAs[TweetEntity] must equal(tweetEntity)
    }
  }
}
