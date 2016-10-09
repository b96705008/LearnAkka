package com.github.b96705008.akka.ch8_http.restapi.db

import com.github.b96705008.akka.ch8_http.restapi.models._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONObjectID}

import scala.concurrent.ExecutionContext

/**
  * Created by roger19890107 on 9/29/16.
  */
object TweetManager {
  import MongoDB._
  import TweetEntity._

  val collection = db[BSONCollection]("tweets")

  def save(tweetEntity: TweetEntity)(implicit ec: ExecutionContext) =
    collection.insert(tweetEntity).map(_ => Created(tweetEntity.id.stringify))

  def findById(id: String)(implicit ec: ExecutionContext) =
    collection.find(queryById(id)).one[TweetEntity]

  def deleteById(id: String)(implicit ec: ExecutionContext) =
    collection.remove(queryById(id)).map(_ => Deleted)

  def find(implicit ec: ExecutionContext) =
    collection.find(emptyQuery).cursor[BSONDocument]().collect[List]()

  private def queryById(id: String) = BSONDocument("_id" -> BSONObjectID(id))

  private def emptyQuery = BSONDocument()
}
