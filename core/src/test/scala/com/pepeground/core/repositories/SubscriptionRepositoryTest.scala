package com.pepeground.core.repositories

import org.flywaydb.core.Flyway
import scalikejdbc.scalatest.AutoRollback
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc._
import scalikejdbc.config.DBs

class SubscriptionRepositoryTest extends FlatSpec with BeforeAndAfter with AutoRollback {
  before {
    DBs.setupAll()

    val flyway: Flyway = new Flyway()
    val dataSource = ConnectionPool.dataSource(ConnectionPool.DEFAULT_NAME)

    flyway.setDataSource(dataSource)
    flyway.baseline()
    flyway.migrate()
  }

  override def fixture(implicit session: DBSession) {
    val chat = ChatRepository.create(1, "Some chat", "private")

    sql"insert into subscriptions values (1, ${chat.id}, ${"Alice"}, 0)".update.apply()
    sql"insert into subscriptions values (2, ${chat.id}, ${"Bob"}, 0)".update.apply()
  }

  behavior of "getList"

  it should "return list of subscriptions" in { implicit session =>
    val subscriptions = SubscriptionRepository.getList()

    assert(subscriptions.size == 2)
  }

  behavior of "updateSubscription"

  it should "update subscription since_id" in { implicit session =>
    val subscriptions = SubscriptionRepository.getList()

    subscriptions.foreach(s => SubscriptionRepository.updateSubscription(s.id, 10))

    SubscriptionRepository.getList().foreach { s =>
      assert(s.sinceId.nonEmpty)
      assert(s.sinceId.get == 10)
    }
  }
}