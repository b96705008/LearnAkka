package com.github.b96705008.akka.ch8_http.restapi.models

import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

import scala.util.Success


case class TweetEntity(id: BSONObjectID = BSONObjectID.generate,
                       author: String,
                       body: String)

object TweetEntity {

  implicit def toTweetEntity(tweet: Tweet): TweetEntity =
    TweetEntity(author = tweet.author, body = tweet.body)

  implicit object TweetEntityBSONReader extends BSONDocumentReader[TweetEntity] {

    override def read(doc: BSONDocument): TweetEntity = {
      TweetEntity(
        id = doc.getAs[BSONObjectID]("_id").get,
        author = doc.getAs[String]("author").get,
        body = doc.getAs[String]("body").get
      )
    }
  }

  implicit object TweetEntityBSONWriter extends BSONDocumentWriter[TweetEntity] {

    override def write(t: TweetEntity): BSONDocument = {
      BSONDocument(
        "_id" -> t.id,
        "author" -> t.author,
        "body" -> t.body
      )
    }
  }
}

object TweetEntityProtocol extends DefaultJsonProtocol {

  implicit object BSONObjectIdProtocol extends RootJsonFormat[BSONObjectID] {
    override def write(obj: BSONObjectID): JsValue = JsString(obj.stringify)

    override def read(json: JsValue): BSONObjectID = json match {
      case JsString(id) => BSONObjectID.parse(id) match {
        case Success(validId) => validId
        case _ => throw DeserializationException("Invalid BSON Object id")
      }
      case _ => throw DeserializationException("BSON Object Id expected")
    }
  }

  implicit val entityFormat = jsonFormat3(TweetEntity.apply)
}
