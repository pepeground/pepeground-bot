package com.pepeground.core.repositories

import java.util.NoSuchElementException

import com.pepeground.core.enums.{ChatType}
import com.pepeground.core.entities.ChatEntity
import scalikejdbc._
import org.joda.time._
import com.pepeground.core.support.PostgreSQLSyntaxSupport._

object ChatRepository {
  private val c = ChatEntity.syntax("c")

  def getList(limit: Int = 20, offset: Int = 0)(implicit session: DBSession): List[ChatEntity] = {
    withSQL {
      select.from(ChatEntity as c).limit(limit).offset(offset)
    }.map(rs => ChatEntity(c)(rs)).list().apply()
  }

  def getChatById(id: Long)(implicit session: DBSession): Option[ChatEntity] = {
    withSQL {
      select.from(ChatEntity as c).where.eq(c.id, id).limit(1)
    }.map(rs => ChatEntity(c)(rs)).single.apply()
  }

  def getOrCreateBy(telegramId: Long, name: String, chatType: String)(implicit  session: DBSession): ChatEntity = {
    getByTelegramId(telegramId) match {
      case Some(chat: ChatEntity) => chat
      case None => create(telegramId, name, chatType)
    }
  }

  def updateRandomChance(id: Long, randomChance: Int)(implicit  session: DBSession): Unit = {
    withSQL {
      update(ChatEntity).set(
        ChatEntity.column.randomChance -> randomChance,
        ChatEntity.column.updatedAt -> new DateTime()
      ).where.eq(ChatEntity.column.id, id)
    }.update().apply()
  }

  def updateRepostChat(id: Long, repostChatUsername: String)(implicit  session: DBSession): Unit = {
    withSQL {
      update(ChatEntity).set(
        ChatEntity.column.repostChatUsername -> repostChatUsername,
        ChatEntity.column.updatedAt -> new DateTime()
      ).where.eq(ChatEntity.column.id, id)
    }.update().apply()
  }

  def updateChat(id: Long, name: Option[String], telegramId: Long)(implicit  session: DBSession): Unit = {
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
