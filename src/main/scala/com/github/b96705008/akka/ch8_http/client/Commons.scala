package com.github.b96705008.akka.ch8_http.client

import spray.json.DefaultJsonProtocol

case class IpInfo(ip: String)

object JsonProtocol extends DefaultJsonProtocol {
  implicit val format = jsonFormat1(IpInfo.apply)
}
