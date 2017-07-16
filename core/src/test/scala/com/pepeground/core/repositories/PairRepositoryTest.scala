package com.pepeground.core.repositories

import org.flywaydb.core.Flyway
import scalikejdbc.scalatest.AutoRollback
import org.scalatest._
import org.scalatest.fixture.FlatSpec

import scalikejdbc.ConnectionPool
import scalikejdbc.config.DBs

class PairRepositoryTest extends FlatSpec with BeforeAndAfter with AutoRollback {
  before {
    DBs.setupAll()

    val flyway: Flyway = new Flyway()
    val dataSource = ConnectionPool.dataSource(ConnectionPool.DEFAULT_NAME)

    flyway.setDataSource(dataSource)
    flyway.baseline()
    flyway.migrate()
  }

  behavior of "createPairBy"

  it should "creates new pair" in { implicit session =>
    val chat = ChatRepository.create(1, "Some chat", "private")
    val word1 = WordRepository.create("hello")
    val word2 = WordRepository.create("world")

    val pair = PairRepository.createPairBy(chat.id, word1, word2)

    assert(pair.firstId == word1)
    assert(pair.secondId == word2)
  }

  behavior of "getPairOrCreateBy"

  it should "return existed pair if pair already exists" in { implicit session =>
    val chat = ChatRepository.create(1, "Some chat", "private")
    val word1 = WordRepository.create("hello")
    val word2 = WordRepository.create("world")

    val existedPair = PairRepository.createPairBy(chat.id, word1, word2)
    val pair = PairRepository.getPairOrCreateBy(chat.id, word1, word2)

    assert(existedPair.id == pair.id)
  }

  it should "return new pair if pair does not exists" in { implicit session =>
    val chat = ChatRepository.create(1, "Some chat", "private")
    val word1 = WordRepository.create("hello")
    val word2 = WordRepository.create("world")
    val word3 = WordRepository.create("scala")

    val existedPair = PairRepository.createPairBy(chat.id, word1, word2)
    val pair = PairRepository.getPairOrCreateBy(chat.id, word1, word3)

    assert(existedPair.id != pair.id)
  }

  behavior of "getPairBy"

  it should "return pair" in { implicit session =>
    val chat = ChatRepository.create(1, "Some chat", "private")

    val word1 = WordRepository.create("hello")
    val word2 = WordRepository.create("world")

    val existedPair = PairRepository.createPairBy(chat.id, word1, word2)
    val pair = PairRepository.getPairBy(chat.id, word1, word2)

    assert(existedPair.id == pair.get.id)
  }
}