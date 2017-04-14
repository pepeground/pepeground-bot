package com.pepeground.core.repositories

import java.util.NoSuchElementException

import com.pepeground.core.enums.{ChatType}
import com.pepeground.core.entities.ChatEntity
import scalikejdbc._
import org.joda.time._
import com.pepeground.core.support.PostgreSQLSyntaxSupport._

object ChatRepository {
  private val c = ChatEntity.syntax("c")

  def getOrCreateBy(telegramId: Long, name: String, chatType: String): ChatEntity = DB localTx { implicit session =>
    getByTelegramId(telegramId) match {
      case Some(chat: ChatEntity) => chat
      case None => create(telegramId, name, chatType)
    }
  }

  def updateRandomChance(id: Long, randomChance: Int): Unit = DB localTx { implicit session =>
    withSQL {
      update(ChatEntity).set(
        ChatEntity.column.randomChance -> randomChance,
        ChatEntity.column.updatedAt -> new DateTime()
      ).where.eq(ChatEntity.column.id, id)
    }.update().apply()
  }

  def updateChat(id: Long, name: Option[String], telegramId: Long): Unit = DB localTx { implicit session =>
    withSQL {
      update(ChatEntity).set(
        ChatEntity.column.name -> name,
        ChatEntity.column.telegramId -> telegramId,
        ChatEntity.column.updatedAt -> new DateTime()
      ).where.eq(ChatEntity.column.id, id)
    }.update.apply()
  }

  def create(telegramId: Long, name: String, chatType: String)(implicit  session: DBSession): ChatEntity = {
    withSQL {
      insert.into(ChatEntity).namedValues(
        ChatEntity.column.telegramId -> telegramId,
        ChatEntity.column.name -> Option(name),
        ChatEntity.column.chatType -> ChatType(chatType.toLowerCase),
        ChatEntity.column.updatedAt -> new DateTime(),
        ChatEntity.column.createdAt -> new DateTime()
      ).onConflictDoNothing()
    }.update().apply()

    getByTelegramId(telegramId) match {
      case Some(chat: ChatEntity) => chat
      case None => throw new NoSuchElementException("No such chat")
    }
  }

  def getByTelegramId(telegramId: Long)(implicit session: DBSession): Option[ChatEntity] = {
    withSQL {
      select.from(ChatEntity as c).where.eq(c.telegramId, telegramId).limit(1)
    }.map(rs => ChatEntity(c)(rs)).single.apply()
  }
}