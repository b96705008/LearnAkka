package com.github.b96705008.akka.ch8_http.restapi.models

import spray.json.DefaultJsonProtocol

/**
  * Created by roger19890107 on 9/29/16.
  */
case class Tweet(author: String, body: String)

object TweetProtocol extends DefaultJsonProtocol {
  implicit val tweetFormat = jsonFormat2(Tweet.apply)
}
