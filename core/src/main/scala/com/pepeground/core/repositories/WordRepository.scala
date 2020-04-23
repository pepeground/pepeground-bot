package com.pepeground.core.repositories

import com.pepeground.core.entities.WordEntity
import scalikejdbc._
import com.pepeground.core.support.PostgreSQLSyntaxSupport._
import scala.util.control.Breaks._
import com.typesafe.scalalogging._
import org.slf4j.LoggerFactory

object WordRepository {
  private val logger = Logger(LoggerFactory.getLogger(this.getClass))
  private val w = WordEntity.syntax("w")

  def getFirstId()(implicit session: DBSession): Option[Long] = {
    withSQL {
      select(sqls"MIN(id)").from(WordEntity as w)
    }.map(rs => rs.long(1)).single.apply()
  }

  def getNextId(id: Long)(implicit session: DBSession): Option[Long] = {
    withSQL {
      select(w.id).from(WordEntity as w).where.gt(w.id, id).orderBy(w.id.asc).limit(1)
    }.map(rs => rs.long("id")).single.apply()
  }

  def deleteById(id: Long)(implicit session: DBSession): Unit = {
    withSQL {
      delete.from(WordEntity as w).where.eq(w.id, id)
    }.update().apply()
  }

  def getByWords(words: List[String])(implicit session: DBSession): List[WordEntity] = {
    withSQL {
      select.from(WordEntity as w).where.in(w.word, words)
    }.map(rs => WordEntity(w)(rs)).list.apply()
  }

  def getWordById(id: Long)(implicit session: DBSession): Option[WordEntity] = {
    withSQL {
      select.from(WordEntity as w).where.eq(w.id, id).limit(1)
    }.map(rs => WordEntity(w)(rs)).single.apply()
  }

  def getByWord(word: String)(implicit session: DBSession): Option[WordEntity] = {
    withSQL {
      select.from(WordEntity as w).where.eq(w.word, word).limit(1)
    }.map(rs => WordEntity(w)(rs)).single.apply()
  }

  def create(word: String)(implicit session: DBSession): Option[Long] = {
    withSQL {
      insert.into(WordEntity).namedValues(
        WordEntity.column.word -> word
      ).onConflictDoNothing().returningId
    }.map(rs => rs.long("id")).single().apply()
  }

  def learnWords(words: List[String])(implicit session: DBSession): Unit = {
    val existedWords: List[String] = getByWords(words).map(_.word)

    words.foreach { word =>
      breakable {
        if (existedWords.contains(word)) break
        logger.info("Learn new word: %s".format(word))
        create(word)
      }
    }
  }
}
