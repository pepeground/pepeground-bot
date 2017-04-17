package com.pepeground.core.entities

import scalikejdbc._
import org.joda.time._

case class PairEntity(id: Long, chatId: Long, firstId: Option[Long], secondId: Option[Long], createdAt: DateTime,
                      replies: Seq[ReplyEntity] = Nil, updatedAt: DateTime)

object PairEntity extends SQLSyntaxSupport[PairEntity] {
  override val tableName = "pairs"
  override val useSnakeCaseColumnName = true

  def apply(g: SyntaxProvider[PairEntity])(rs: WrappedResultSet): PairEntity = apply(g.resultName)(rs)
  def apply(c: ResultName[PairEntity])(rs: WrappedResultSet) = new PairEntity(
    rs.long(c.id),
    rs.long(c.chatId),
    rs.longOpt(c.firstId),
    rs.longOpt(c.secondId),
    rs.jodaDateTime(c.createdAt),
    updatedAt = rs.jodaDateTime(c.updatedAt)
  )
}