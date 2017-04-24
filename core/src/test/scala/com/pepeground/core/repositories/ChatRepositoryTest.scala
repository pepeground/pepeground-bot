package com.pepeground.core.repositories

import com.pepeground.core.enums.ChatType
import org.flywaydb.core.Flyway
import org.scalatest._
import scalikejdbc.ConnectionPool
import scalikejdbc.config.DBs
import scalikejdbc._

class ChatRepositoryTest extends FunSpec with Matchers with BeforeAndAfter {
  before {
    DBs.setupAll()

    val flyway: Flyway = new Flyway()
    val dataSource = ConnectionPool.dataSource(ConnectionPool.DEFAULT_NAME)

    flyway.setDataSource(dataSource)
    flyway.baseline()
    flyway.migrate()
  }

  describe("create") {
    it("creates new chat") {
      val newChat = DB localTx { implicit session => ChatRepository.create(1, "Some chat", "private") }
      assert(newChat.telegramId == 1)
      assert(newChat.name.get == "Some chat")
      assert(newChat.chatType == ChatType("private"))
    }
  }

  describe("getChatById") {
    it("returns chat by id") {
      val newChat = DB localTx { implicit session => ChatRepository.create(1, "Some chat", "private") }
      val chat = DB readOnly { implicit session => ChatRepository.getChatById(newChat.id) }

      assert(newChat.id == chat.get.id)
    }
  }

  describe("getOrCreateBy") {
    describe("chat already exists") {
      it("returns existed chat") {
        val newChat = DB localTx { implicit session => ChatRepository.create(1, "Some chat", "private") }
        val chat = DB readOnly { implicit session =>
          ChatRepository.getOrCreateBy(newChat.telegramId, newChat.name.get, ChatType(newChat.chatType))
        }

        assert(chat.id == newChat.id)
      }
    }

    describe("chat not exists") {
      it("returns new chat") {
        val newChat = DB localTx { implicit session => ChatRepository.create(1, "Some chat", "private") }
        val chat = DB readOnly { implicit session =>
          ChatRepository.getOrCreateBy(2, newChat.name.get, ChatType(newChat.chatType))
        }

        assert(chat.id != newChat.id)
      }
    }
  }

  describe("updateRandomChance") {
    it("updates random chance") {
      val newChat = DB localTx { implicit session => ChatRepository.create(1, "Some chat", "private") }
      DB localTx { implicit session => ChatRepository.updateRandomChance(newChat.id, 50) }
      val updatedChat = DB readOnly { implicit session => ChatRepository.getChatById(newChat.id) }

      assert(newChat.randomChance != updatedChat.get.randomChance)
    }
  }

  describe("updateRepostChat") {
    it("updates repost chat") {
      val newChat = DB localTx { implicit session => ChatRepository.create(1, "Some chat", "private") }
      DB localTx { implicit session => ChatRepository.updateRepostChat(newChat.id, "@ti_pidor") }
      val updatedChat = DB readOnly { implicit session => ChatRepository.getChatById(newChat.id) }

      assert(newChat.repostChatUsername != updatedChat.get.repostChatUsername)
    }
  }

  describe("updateChat") {
    it("updates chat") {
      val newChat = DB localTx { implicit session => ChatRepository.create(1, "Some chat", "private") }
      DB localTx { implicit session => ChatRepository.updateChat(newChat.id, Some("KEK"), 3) }
      val updatedChat = DB readOnly { implicit session => ChatRepository.getChatById(newChat.id) }

      assert(newChat.id == updatedChat.get.id)
      assert(newChat.name != updatedChat.get.name)
      assert(newChat.telegramId != updatedChat.get.telegramId)
    }
  }

  describe("getByTelegramId") {
    it("returns chat by telegram id") {
      val newChat = DB localTx { implicit session => ChatRepository.create(4, "Some chat", "private") }
      val byTelegramId = DB readOnly { implicit session => ChatRepository.getByTelegramId(newChat.telegramId) }

      assert(newChat.id == byTelegramId.get.id)
    }
  }
}
