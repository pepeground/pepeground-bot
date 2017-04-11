package com.pepeground.bot.repositories

import java.util.NoSuchElementException

import com.pepeground.bot.enums.{ChatType}
import com.pepeground.bot.entities.ChatEntity
import scalikejdbc._
import org.joda.time._

object ChatRepository {
  private val c = ChatEntity.syntax("c")

  def getOrCreateBy(telegramId: Long, name: String, chatType: String): ChatEntity = DB localTx { implicit session =>
    getByTelegramId(telegramId) match {
      case Some(chat: ChatEntity) => chat
      case None => create(telegramId, name, chatType)
    }
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
        c.telegramId -> telegramId,
        c.name -> Option(name),
        c.chatType -> ChatType(chatType.toLowerCase),
        c.updatedAt -> new DateTime(),
        c.createdAt -> new DateTime()
      )
    }.update().apply()

    getByTelegramId(telegramId) match {
      case Some(chat: ChatEntity) => chat
      case None => throw new NoSuchElementException("No such chat")
    }
  }

  def getByTelegramId(telegramId: Long)(implicit session: DBSession): Option[ChatEntity] = {
    withSQL {
      select.from(ChatEntity as c).where.eq(c.telegramId, telegramId)
    }.map(rs => ChatEntity(c)(rs)).single.apply()
  }
}