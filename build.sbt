name := "LearnAkka"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.10",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.10",
  "com.typesafe.akka" %% "akka-persistence-query-experimental" % "2.4.10",
  "com.typesafe.akka" %% "akka-remote" % "2.4.10",
  "com.typesafe.akka" %% "akka-cluster" % "2.4.10",
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.4.10",
  "com.typesafe.akka" %% "akka-cluster-sharding" % "2.4.10",
  "com.typesafe.akka" %% "akka-contrib" % "2.4.10",
  "com.typesafe.akka" %% "akka-stream" % "2.4.10",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.10",
  "org.iq80.leveldb" % "leveldb" % "0.7",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.10",
  "org.twitter4j" % "twitter4j-stream" % "4.0.2",
  "com.typesafe.akka" %% "akka-http-core" % "2.4.10",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.10",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.10",
  "com.typesafe.akka" %% "akka-http-testkit" % "2.4.10",
  "org.reactivemongo" %% "reactivemongo" % "0.11.7"
)

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Typesafe" at "https://repo.typesafe.com/typesafe/releases/"
