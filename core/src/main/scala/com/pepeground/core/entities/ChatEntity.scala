package com.pepeground.core.entities

import scalikejdbc._
import org.joda.time._

case class ChatEntity(id: Long, name: Option[String], telegramId: Long, chatType: Int, randomChance: Int,
                      createdAt: DateTime, updatedAt: DateTime)

object ChatEntity extends SQLSyntaxSupport[ChatEntity] {
  override val tableName = "chats"
  override val useSnakeCaseColumnName = true

  def apply(g: SyntaxProvider[ChatEntity])(rs: WrappedResultSet): ChatEntity = apply(g.resultName)(rs)
  def apply(c: ResultName[ChatEntity])(rs: WrappedResultSet) = new ChatEntity(
    rs.long(c.id),
    rs.stringOpt(c.name),
    rs.long(c.telegramId),
    rs.int(c.chatType),
    rs.int(c.randomChance),
    rs.jodaDateTime(c.createdAt),
    rs.jodaDateTime(c.updatedAt)
  )
}