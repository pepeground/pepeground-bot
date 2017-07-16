package com.pepeground.core.repositories

import org.flywaydb.core.Flyway
import scalikejdbc.scalatest.AutoRollback
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc.ConnectionPool
import scalikejdbc.config.DBs

class ReplyRepositoryTest extends FlatSpec with BeforeAndAfter with AutoRollback {
  before {
    DBs.setupAll()

    val flyway: Flyway = new Flyway()
    val dataSource = ConnectionPool.dataSource(ConnectionPool.DEFAULT_NAME)

    flyway.setDataSource(dataSource)
    flyway.baseline()
    flyway.migrate()
  }

  behavior of "createReplyBy"

  it should "creates new reply" in { implicit session =>
    val chat = ChatRepository.create(1, "Some chat", "private")
    val word1 = WordRepository.create("hello")
    val word2 = WordRepository.create("world")
    val word3 = WordRepository.create("scala")

    val pair = PairRepository.createPairBy(chat.id, word1, word2)

    val reply = ReplyRepository.createReplyBy(pair.id, word3)

    assert(reply.pairId == pair.id)
    assert(reply.wordId == word3)
  }

  behavior of "getReplyBy"

  it should "return reply" in { implicit session =>
    val chat = ChatRepository.create(1, "Some chat", "private")
    val word1 = WordRepository.create("hello")
    val word2 = WordRepository.create("world")
    val word3 = WordRepository.create("scala")

    val pair = PairRepository.createPairBy(chat.id, word1, word2)

    val reply = ReplyRepository.createReplyBy(pair.id, word3)

    val sameReply = ReplyRepository.getReplyBy(pair.id, word3)

    assert(sameReply.nonEmpty)
    assert(sameReply.get.id == reply.id)
  }

  behavior of "incrementReply"

  it should "increment counter by 1" in { implicit session =>
    val chat = ChatRepository.create(1, "Some chat", "private")
    val word1 = WordRepository.create("hello")
    val word2 = WordRepository.create("world")
    val word3 = WordRepository.create("scala")

    val pair = PairRepository.createPairBy(chat.id, word1, word2)

    val reply = ReplyRepository.createReplyBy(pair.id, word3)

    ReplyRepository.incrementReply(reply.id, reply.count)

    val sameReply = ReplyRepository.getReplyBy(pair.id, word3)

    assert(sameReply.nonEmpty)
    assert(sameReply.get.id == reply.id)
    assert(sameReply.get.count > reply.count)
  }

  behavior of "repliesForPair"

  it should "return pair replies" in { implicit session =>
    val chat = ChatRepository.create(1, "Some chat", "private")
    val word1 = WordRepository.create("hello")
    val word2 = WordRepository.create("world")
    val word3 = WordRepository.create("scala")

    val pair = PairRepository.createPairBy(chat.id, word1, word2)

    val reply = ReplyRepository.createReplyBy(pair.id, word3)

    val replies = ReplyRepository.repliesForPair(pair.id).map(_.id)

    assert(replies == List(reply.id))
  }

  behavior of "getReplyOrCreateBy"

  it should "return existed reply if reply not exists" in { implicit session =>
    val chat = ChatRepository.create(1, "Some chat", "private")
    val word1 = WordRepository.create("hello")
    val word2 = WordRepository.create("world")
    val word3 = WordRepository.create("scala")

    val pair = PairRepository.createPairBy(chat.id, word1, word2)

    val reply = ReplyRepository.createReplyBy(pair.id, word3)

    val sameReply = ReplyRepository.getReplyOrCreateBy(pair.id, word3)

    assert(reply.id == sameReply.id)
  }
}