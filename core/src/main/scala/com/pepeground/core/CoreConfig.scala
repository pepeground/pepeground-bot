package com.pepeground.core

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._

object CoreConfig extends CoreConfig

class CoreConfig {
  lazy val config = ConfigFactory.load()

  object redis {
    private lazy val redisConfig = config.getConfig("redis")
    lazy val host = redisConfig.getString("host")
    lazy val port = redisConfig.getInt("port")
  }

  object punctuation {
    private lazy val punctuationConfig = config.getConfig("punctuation")

    lazy val endSentence: List[String] = punctuationConfig.getStringList("endSentence").asScala.toList
  }
}