package com.github.b96705008.akka.ch7_streams.twitter

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import twitter4j.auth.AccessToken
import twitter4j._
import twitter4j.conf.ConfigurationBuilder

import scala.collection.JavaConverters._

object TwitterConfiguration {
  val config = ConfigFactory.load("twitter").getConfig("Twitter")

  val apiKey = config.getString("apiKey")
  val apiSecret = config.getString("apiSecret")
  val accessToken = config.getString("accessToken")
  val accessTokenSecret = config.getString("accessTokenSecret")
}

object TwitterClient {
  def getInstance: Twitter = {
    val cb = new ConfigurationBuilder()
    cb.setDebugEnabled(true)
      .setOAuthConsumerKey(TwitterConfiguration.apiKey)
      .setOAuthConsumerSecret(TwitterConfiguration.apiSecret)
      .setOAuthAccessToken(TwitterConfiguration.accessToken)
      .setOAuthAccessTokenSecret(TwitterConfiguration.accessTokenSecret)

    val tf = new TwitterFactory(cb.build())
    tf.getInstance()
  }

  def retrieveTweets(term: String) = {
    val query = new Query(term)
    query.setCount(100)
    getInstance.search(query).getTweets.asScala.iterator
  }
}

class TwitterStreamClient(val actorSystem: ActorSystem) {
  val factory = new TwitterStreamFactory(new ConfigurationBuilder().build())
  val twitterStream = factory.getInstance()

  def init = {
    twitterStream.setOAuthConsumer(TwitterConfiguration.apiKey, TwitterConfiguration.apiSecret)
    twitterStream.setOAuthAccessToken(new AccessToken(TwitterConfiguration.accessToken, TwitterConfiguration.accessTokenSecret))
    twitterStream.addListener(statusListener)
    twitterStream.sample()
  }

  def statusListener = new StatusListener {
    override def onStallWarning(warning: StallWarning): Unit = {}

    override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit = {}

    override def onScrubGeo(userId: Long, upToStatusId: Long): Unit = {}

    override def onStatus(status: Status): Unit = {
      actorSystem.eventStream.publish(Tweet(Author(status.getUser.getScreenName), status.getText))
    }

    override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit = {}

    override def onException(ex: Exception): Unit = {
      ex.printStackTrace()
    }
  }

  def stop = {
    twitterStream.cleanUp()
    twitterStream.shutdown()
  }

}