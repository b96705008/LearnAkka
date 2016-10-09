package com.github.b96705008.akka.ch7_streams.twitter

/**
  * Created by roger19890107 on 9/25/16.
  */
case class Author(name: String)

case class HashTag(name: String) {
  require(name.startsWith("#"), "Hash tag must start with #")
}

case class Tweet(author: Author, body: String) {
  def hashTags: Set[HashTag] = {
    body.split(" ").collect {
      case t if t.startsWith("#") => HashTag(t)
    }.toSet
  }
}
