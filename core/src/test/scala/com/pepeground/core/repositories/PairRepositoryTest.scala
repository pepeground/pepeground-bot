package com.pepeground.core.repositories

import org.flywaydb.core.Flyway
import org.scalatest._
import scalikejdbc.ConnectionPool
import scalikejdbc.config.DBs
import scalikejdbc._

class PairRepositoryTest extends FunSpec with Matchers with BeforeAndAfter {
  before {
    DBs.setupAll()

    val flyway: Flyway = new Flyway()
    val dataSource = ConnectionPool.dataSource(ConnectionPool.DEFAULT_NAME)

    flyway.setDataSource(dataSource)
    flyway.baseline()
    flyway.migrate()
  }

  describe("createPairBy") {
    it("creates new pair") {
      val chat = DB localTx { implicit session => ChatRepository.create(1, "Some chat", "private") }
      val word1 = DB localTx { implicit session => WordRepository.create("hello") }
      val word2 = DB localTx { implicit session => WordRepository.create("world") }

      val pair = DB localTx { implicit session => PairRepository.createPairBy(chat.id, word1, word2) }

      assert(pair.firstId == word1)
      assert(pair.secondId == word2)
    }
  }

  describe("getPairOrCreateBy") {
    describe("when pair already exists") {
      it("it returns existed pair") {
        val chat = DB localTx { implicit session => ChatRepository.create(1, "Some chat", "private") }
        val word1 = DB localTx { implicit session => WordRepository.create("hello") }
        val word2 = DB localTx { implicit session => WordRepository.create("world") }

        val existedPair = DB localTx { implicit session => PairRepository.createPairBy(chat.id, word1, word2) }
        val pair = DB localTx { implicit session => PairRepository.getPairOrCreateBy(chat.id, word1, word2) }

        assert(existedPair.id == pair.id)
      }
    }

    describe("new pair") {
      it("returns new pair") {
        val chat = DB localTx { implicit session => ChatRepository.create(1, "Some chat", "private") }
        val word1 = DB localTx { implicit session => WordRepository.create("hello") }
        val word2 = DB localTx { implicit session => WordRepository.create("world") }
        val word3 = DB localTx { implicit session => WordRepository.create("scala") }

        val existedPair = DB localTx { implicit session => PairRepository.createPairBy(chat.id, word1, word2) }
        val pair = DB localTx { implicit session => PairRepository.getPairOrCreateBy(chat.id, word1, word3) }

        assert(existedPair.id != pair.id)
      }
    }
  }

  describe("getPairBy") {
    it("returns pair") {
      val chat = DB localTx { implicit session => ChatRepository.create(1, "Some chat", "private") }

      val word1 = DB localTx { implicit session => WordRepository.create("hello") }
      val word2 = DB localTx { implicit session => WordRepository.create("world") }

      val existedPair = DB localTx { implicit session => PairRepository.createPairBy(chat.id, word1, word2) }
      val pair = DB readOnly { implicit session => PairRepository.getPairBy(chat.id, word1, word2)}

      assert(existedPair.id == pair.get.id)
    }
  }
}