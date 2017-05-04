package com.pepeground.core.repositories

import com.pepeground.core.entities.SubscriptionEntity
import scalikejdbc._

object SubscriptionRepository {
  private val c = SubscriptionEntity.syntax("c")

  def getList()(implicit session: DBSession): List[SubscriptionEntity] = {
    withSQL {
      select.from(SubscriptionEntity as c)
    }.map(rs => SubscriptionEntity(c)(rs)).list().apply()
  }

  def updateSubscription(id: Long, sinceId: Long): Unit = DB localTx { implicit session =>
    withSQL {
      update(SubscriptionEntity).set(
        SubscriptionEntity.column.sinceId -> sinceId
      ).where.eq(SubscriptionEntity.column.id, id)
    }.update.apply()
  }
}