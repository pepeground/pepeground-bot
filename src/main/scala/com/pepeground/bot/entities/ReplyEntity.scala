package com.pepeground.bot.entities

import scalikejdbc._

case class ReplyEntity(id: Long, pairId: Long, wordId: Option[Long], count: Long)

object ReplyEntity extends SQLSyntaxSupport[ReplyEntity] {
  override val tableName = "replies"
  override val useSnakeCaseColumnName = true

  def apply(g: SyntaxProvider[ReplyEntity])(rs: WrappedResultSet): ReplyEntity = apply(g.resultName)(rs)
  def apply(c: ResultName[ReplyEntity])(rs: WrappedResultSet) = new ReplyEntity(
    rs.long(c.id),
    rs.long(c.pairId),
    rs.longOpt(c.wordId),
    rs.long(c.count)
  )

  def opt(m: SyntaxProvider[ReplyEntity])(rs: WrappedResultSet): Option[ReplyEntity] =
    rs.longOpt(m.resultName.id).map(_ => ReplyEntity(m)(rs))
}