package com.pepeground.core.entities

import scalikejdbc._

case class SubscriptionEntity(id: Long, name: String, chatId: Long, sinceId: Option[Long])

object SubscriptionEntity extends SQLSyntaxSupport[SubscriptionEntity] {
  override val tableName = "subscriptions"
  override val useSnakeCaseColumnName = true

  def apply(g: SyntaxProvider[SubscriptionEntity])(rs: WrappedResultSet): SubscriptionEntity = apply(g.resultName)(rs)
  def apply(c: ResultName[SubscriptionEntity])(rs: WrappedResultSet) = new SubscriptionEntity(
    rs.long(c.id),
    rs.string(c.name),
    rs.long(c.chatId),
    rs.longOpt(c.sinceId)
  )
}