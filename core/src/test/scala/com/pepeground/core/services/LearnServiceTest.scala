package com.pepeground.core.services

import com.pepeground.core.repositories.{ChatRepository, PairRepository, ReplyRepository, WordRepository}
import org.flywaydb.core.Flyway
import scalikejdbc.scalatest.AutoRollback
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc.ConnectionPool
import scalikejdbc.config.DBs

class LearnServiceTest extends FlatSpec with BeforeAndAfter with AutoRollback {
  before {
    DBs.setupAll()

    val flyway: Flyway = new Flyway()
    val dataSource = ConnectionPool.dataSource(ConnectionPool.DEFAULT_NAME)

    flyway.setDataSource(dataSource)
    flyway.baseline()
    flyway.migrate()
  }

  behavior of "learn"

  it should "learn pairs" in { implicit session =>
    val newChat = ChatRepository.create(4, "Some chat", "private")
    val service = new LearnService(List("hello", "world", "scala"), newChat.id)

    service.learnPair()

    val word1 = WordRepository.getByWord("hello")
    val word2 = WordRepository.getByWord("world")
    val word3 = WordRepository.getByWord("scala")

    assert(word1.nonEmpty)
    assert(word2.nonEmpty)
    assert(word3.nonEmpty)

    assert(PairRepository.getPairsCount(newChat.id) == 5)

    val pair1 = PairRepository.getPairBy(newChat.id, None, Some(word1.get.id))
    val pair2 = PairRepository.getPairBy(newChat.id, Some(word1.get.id), Some(word2.get.id))
    val pair3 = PairRepository.getPairBy(newChat.id, Some(word2.get.id), Some(word3.get.id))
    val pair4 = PairRepository.getPairBy(newChat.id, Some(word3.get.id), None)
    val pair5 = PairRepository.getPairBy(newChat.id, None, None)

    assert(pair1.nonEmpty)
    assert(pair2.nonEmpty)
    assert(pair3.nonEmpty)
    assert(pair4.nonEmpty)
    assert(pair5.nonEmpty)

    val reply1 = ReplyRepository.getReplyBy(pair1.get.id, Some(word2.get.id))
    val reply2 = ReplyRepository.getReplyBy(pair2.get.id, Some(word3.get.id))
    val reply3 = ReplyRepository.getReplyBy(pair3.get.id, None)
    val reply4 = ReplyRepository.getReplyBy(pair4.get.id, None)
    val reply5 = ReplyRepository.getReplyBy(pair5.get.id, None)

    assert(reply1.nonEmpty)
    assert(reply2.nonEmpty)
    assert(reply3.nonEmpty)
    assert(reply4.nonEmpty)
    assert(reply5.nonEmpty)
  }

  it should "learn pairs once" in { implicit session =>
    0 to 1 foreach { i =>
      val newChat = ChatRepository.create(4, "Some chat", "private")
      val service = new LearnService(List("hello", "world", "scala"), newChat.id)

      service.learnPair()

      val word1 = WordRepository.getByWord("hello")
      val word2 = WordRepository.getByWord("world")
      val word3 = WordRepository.getByWord("scala")

      assert(word1.nonEmpty)
      assert(word2.nonEmpty)
      assert(word3.nonEmpty)

      assert(PairRepository.getPairsCount(newChat.id) == 5)

      val pair1 = PairRepository.getPairBy(newChat.id, None, Some(word1.get.id))
      val pair2 = PairRepository.getPairBy(newChat.id, Some(word1.get.id), Some(word2.get.id))
      val pair3 = PairRepository.getPairBy(newChat.id, Some(word2.get.id), Some(word3.get.id))
      val pair4 = PairRepository.getPairBy(newChat.id, Some(word3.get.id), None)
      val pair5 = PairRepository.getPairBy(newChat.id, None, None)

      assert(pair1.nonEmpty)
      assert(pair2.nonEmpty)
      assert(pair3.nonEmpty)
      assert(pair4.nonEmpty)
      assert(pair5.nonEmpty)

      val reply1 = ReplyRepository.getReplyBy(pair1.get.id, Some(word2.get.id))
      val reply2 = ReplyRepository.getReplyBy(pair2.get.id, Some(word3.get.id))
      val reply3 = ReplyRepository.getReplyBy(pair3.get.id, None)
      val reply4 = ReplyRepository.getReplyBy(pair4.get.id, None)
      val reply5 = ReplyRepository.getReplyBy(pair5.get.id, None)

      assert(reply1.nonEmpty)
      assert(reply2.nonEmpty)
      assert(reply3.nonEmpty)
      assert(reply4.nonEmpty)
      assert(reply5.nonEmpty)
    }
  }

  it should "learn pairs with words which contains punctuation chars" in { implicit session =>
    val newChat = ChatRepository.create(4, "Some chat", "private")
    val service = new LearnService(List("hello", "world.", "scala"), newChat.id)

    service.learnPair()

    val word1 = WordRepository.getByWord("hello")
    val word2 = WordRepository.getByWord("world.")
    val word3 = WordRepository.getByWord("scala")

    assert(word1.nonEmpty)
    assert(word2.nonEmpty)
    assert(word3.nonEmpty)

    assert(PairRepository.getPairsCount(newChat.id) == 6)

    val pair1 = PairRepository.getPairBy(newChat.id, None, Some(word1.get.id))
    val pair2 = PairRepository.getPairBy(newChat.id, Some(word1.get.id), Some(word2.get.id))
    val pair3 = PairRepository.getPairBy(newChat.id, Some(word2.get.id), None)
    val pair4 = PairRepository.getPairBy(newChat.id, None, Some(word3.get.id))
    val pair5 = PairRepository.getPairBy(newChat.id, Some(word3.get.id), None)
    val pair6 = PairRepository.getPairBy(newChat.id, None, None)

    assert(pair1.nonEmpty)
    assert(pair2.nonEmpty)
    assert(pair3.nonEmpty)
    assert(pair4.nonEmpty)
    assert(pair5.nonEmpty)
    assert(pair6.nonEmpty)

    val reply1 = ReplyRepository.getReplyBy(pair1.get.id, Some(word2.get.id))
    val reply2 = ReplyRepository.getReplyBy(pair2.get.id, None)
    val reply3 = ReplyRepository.getReplyBy(pair3.get.id, Some(word3.get.id))
    val reply4 = ReplyRepository.getReplyBy(pair4.get.id, None)
    val reply5 = ReplyRepository.getReplyBy(pair5.get.id, None)
    val reply6 = ReplyRepository.getReplyBy(pair6.get.id, None)

    assert(reply1.nonEmpty)
    assert(reply2.nonEmpty)
    assert(reply3.nonEmpty)
    assert(reply4.nonEmpty)
    assert(reply5.nonEmpty)
    assert(reply6.nonEmpty)
  }
}