package com.pepeground.bot.repositories

import java.util.NoSuchElementException

import com.pepeground.bot.entities.{PairEntity, ReplyEntity}
import org.joda.time.DateTime
import scalikejdbc._
import sqls.{ distinct, count }
import com.pepeground.bot.support.PostgreSQLSyntaxSupport._

object PairRepository {
  private val p = PairEntity.syntax("p")
  private val r = ReplyEntity.syntax("r")

  def getPairWithReplies(chatId: Long, firstIds: Option[Long], secondIds: List[Option[Long]])(implicit session: DBSession): Option[PairEntity] = {
    var timeOffset = new DateTime

    timeOffset = timeOffset.minusMinutes(10)

    withSQL {
      select
        .from(PairEntity as p)
        .leftJoin(ReplyEntity as r)
        .on(p.id, r.pairId)
        .where.lt(p.createdAt, timeOffset)
        .and.eq(p.chatId, chatId)
        .and.eq(p.firstId, firstIds)
        .and.in(p.secondId, secondIds)
        .orderBy(sqls"random()")
        .limit(1)
    }.one(PairEntity(p))
      .toMany(ReplyEntity.opt(r))
      .map { (pair, replies) => pair.copy(replies = replies) }
      .single
      .apply()
  }

  def getPairBy(chatId: Long, firstId: Option[Long], secondId: Option[Long])(implicit session: DBSession): Option[PairEntity] = {
    withSQL {
      select.from(PairEntity as p).where.eq(p.chatId, chatId).and.eq(p.firstId, firstId).and.eq(p.secondId, secondId).limit(1)
    }.map(rs => PairEntity(p)(rs)).single.apply()
  }

  def createPairBy(chatId: Long, firstId: Option[Long], secondId: Option[Long])(implicit session: DBSession): PairEntity = {
    withSQL {
      insert.into(PairEntity).namedValues(
        PairEntity.column.chatId -> chatId,
        PairEntity.column.firstId -> firstId,
        PairEntity.column.secondId -> secondId,
        PairEntity.column.createdAt -> new DateTime()
      ).onConflictDoNothing()
    }.update().apply()

    getPairBy(chatId, firstId, secondId) match {
      case Some(pair: PairEntity) => pair
      case None => throw new NoSuchElementException("No such pair")
    }
  }

  def getPairsCount(chatId: Long)(implicit session: DBSession): Int = {
    withSQL {
      select(count(distinct(p.id))).from(PairEntity as p).where.eq(p.chatId, chatId)
    }.map(_.int(1)).single.apply().get
  }

  def getPairOrCreateBy(chatId: Long, firstId: Option[Long], secondId: Option[Long])(implicit session: DBSession) = {
    getPairBy(chatId, firstId, secondId) match {
      case Some(p: PairEntity) => p
      case None => createPairBy(chatId, firstId, secondId)
    }
  }
}