package com.pepeground.core.services

import com.pepeground.core.repositories.{ChatRepository, PairRepository, ReplyRepository, WordRepository}
import org.flywaydb.core.Flyway
import org.joda.time.DateTime
import scalikejdbc.scalatest.AutoRollback
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc._
import scalikejdbc.config.DBs

import scala.io.Source

class StoryServiceTest extends FlatSpec with BeforeAndAfter with AutoRollback {
  before {
    DBs.setupAll()

    val flyway: Flyway = new Flyway()
    val dataSource = ConnectionPool.dataSource(ConnectionPool.DEFAULT_NAME)

    flyway.setDataSource(dataSource)
    flyway.baseline()
    flyway.migrate()
  }

  behavior of "generate"

  it should "generate empty on new dictionary" in { implicit session =>
    val newChat = ChatRepository.create(4, "Some chat", "private")
    val learnService = new LearnService(List("hello", "world", "scala"), newChat.id)

    learnService.learnPair()

    val storySetvice = new StoryService(List("hello", "world", "scala"), List(), newChat.id)

    val message = storySetvice.generate()

    assert(message.isEmpty)
  }

  it should "generate empty on empty dictionary" in { implicit session =>
    val newChat = ChatRepository.create(4, "Some chat", "private")

    val storySetvice = new StoryService(List("hello", "world", "scala"), List(), newChat.id)

    val message = storySetvice.generate()

    assert(message.isEmpty)
  }

  it should "generate non-empty message" in { implicit session =>
    val newChat = ChatRepository.create(4, "Some chat", "private")

    new LearnService(List("hello", "world", "scala"), newChat.id).learnPair()

    var timeOffset = new DateTime().minusMinutes(10)

    sql"UPDATE pairs SET created_at = ${timeOffset}".update.apply()

    val storySetvice = new StoryService(List("hello", "world", "scala"), List(), newChat.id)

    val message = storySetvice.generate()

    assert(message.nonEmpty)
    assert(message.get.contains("hello world scala"))
  }

  it should "generate with punctuation chars" in { implicit session =>
    val newChat = ChatRepository.create(4, "Some chat", "private")

    new LearnService(List("hello", "world.", "scala"), newChat.id).learnPair()

    var timeOffset = new DateTime().minusMinutes(10)

    sql"UPDATE pairs SET created_at = ${timeOffset}".update.apply()

    val storySetvice = new StoryService(List("hello", "world.", "scala"), List(), newChat.id)

    val message = storySetvice.generate()

    assert(message.nonEmpty)
    assert(message.get.contains("hello world. scala"))
  }
}