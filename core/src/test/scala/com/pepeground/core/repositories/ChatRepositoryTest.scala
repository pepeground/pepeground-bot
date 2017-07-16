package com.pepeground.core.repositories

import com.pepeground.core.enums.ChatType
import org.flywaydb.core.Flyway
import scalikejdbc._
import scalikejdbc.scalatest.AutoRollback
import org.scalatest._
import org.scalatest.fixture.FlatSpec

import scalikejdbc.ConnectionPool
import scalikejdbc.config.DBs

class ChatRepositoryTest extends FlatSpec with BeforeAndAfter with AutoRollback {
  before {
    DBs.setupAll()

    val flyway: Flyway = new Flyway()
    val dataSource = ConnectionPool.dataSource(ConnectionPool.DEFAULT_NAME)

    flyway.setDataSource(dataSource)
    flyway.baseline()
    flyway.migrate()
  }

  behavior of "Create"

  it should "create new chat" in { implicit session =>
    val newChat = ChatRepository.create(1, "Some chat", "private")
    assert(newChat.telegramId == 1)
    assert(newChat.name.get == "Some chat")
    assert(newChat.chatType == ChatType("private"))
  }

  behavior of "getChatById"

  it should "return chat by id" in { implicit session =>
    val newChat = ChatRepository.create(1, "Some chat", "private")
    val chat = ChatRepository.getChatById(newChat.id)

    assert(newChat.id == chat.get.id)
  }

  behavior of "getChatById"

  it should "return exited chat if chat already existed" in { implicit session =>
    val newChat = ChatRepository.create(1, "Some chat", "private")
    val chat = ChatRepository.getOrCreateBy(newChat.telegramId, newChat.name.get, ChatType(newChat.chatType))

    assert(chat.id == newChat.id)
  }

  it should "return new chat if chat not existed" in { implicit session =>
    val newChat = ChatRepository.create(1, "Some chat", "private")
    val chat = ChatRepository.getOrCreateBy(2, newChat.name.get, ChatType(newChat.chatType))

    assert(chat.id != newChat.id)
  }

  behavior of "updateRandomChance"

  it should "update random chance" in { implicit session =>
    val newChat = ChatRepository.create(1, "Some chat", "private")
    ChatRepository.updateRandomChance(newChat.id, 50)

    val updatedChat = ChatRepository.getChatById(newChat.id)

    assert(newChat.randomChance != updatedChat.get.randomChance)
  }

  behavior of "updateRepostChat"

  it should "update repost chat" in { implicit session =>
    val newChat = ChatRepository.create(1, "Some chat", "private")
    ChatRepository.updateRepostChat(newChat.id, "@ti_pidor")

    val updatedChat = ChatRepository.getChatById(newChat.id)

    assert(newChat.repostChatUsername != updatedChat.get.repostChatUsername)
  }

  behavior of "updateChat"

  it should "update chat" in { implicit session =>
    val newChat = ChatRepository.create(1, "Some chat", "private")
    ChatRepository.updateChat(newChat.id, Some("KEK"), 3)
    val updatedChat = ChatRepository.getChatById(newChat.id)

    assert(newChat.id == updatedChat.get.id)
    assert(newChat.name != updatedChat.get.name)
    assert(newChat.telegramId != updatedChat.get.telegramId)
  }

  behavior of "getByTelegramId"

  it should "return chat by telegram id" in { implicit session =>
    val newChat = ChatRepository.create(4, "Some chat", "private")
    val byTelegramId = ChatRepository.getByTelegramId(newChat.telegramId)

    assert(newChat.id == byTelegramId.get.id)
  }
}
