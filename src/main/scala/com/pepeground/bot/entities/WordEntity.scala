package com.pepeground.bot.entities

import scalikejdbc._

case class WordEntity(id: Long, word: String)

object WordEntity extends SQLSyntaxSupport[WordEntity] {
  override val tableName = "words"
  override val useSnakeCaseColumnName = true

  def apply(g: SyntaxProvider[WordEntity])(rs: WrappedResultSet): WordEntity = apply(g.resultName)(rs)
  def apply(c: ResultName[WordEntity])(rs: WrappedResultSet) = new WordEntity(
    rs.long(c.id),
    rs.string(c.word)
  )
}