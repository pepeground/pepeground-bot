package com.bot4s.telegram.future

import java.lang.RuntimeException

import com.bot4s.telegram.api.{TelegramApiException, Polling => BasePolling}
import com.bot4s.telegram.methods.{DeleteWebhook, GetMe}
import com.bot4s.telegram.models.{Update, User}
import slogging.StrictLogging

import scala.concurrent.Future
import scala.util.control.NonFatal

trait TelegramPolling extends BasePolling[Future] with BotExecutionContext with StrictLogging {

  private type OffsetUpdates = (Option[Long], Option[Long], Seq[Update], User)

  @volatile private var polling: Future[Unit] = _

  private def poll(seed: Future[OffsetUpdates]): Future[OffsetUpdates] = {
    seed.flatMap {
      case (previous, offset, updates, user) =>

        val maxOffset = updates
          .map(_.updateId)
          .foldLeft(offset) {
            (acc, e) =>
              Some(acc.fold(e)(e max _))
          }

        // Spawn next request before processing updates.
        val f = if (polling == null) seed
        else
          poll(
            pollingGetUpdates(maxOffset.map(_ + 1).orElse(Option(-1))).recover {
              case NonFatal(e) =>
                logger.error("GetUpdates failed", e)
                Seq.empty[Update]
            }.map { newUpdates =>
              if (maxOffset != previous || newUpdates != updates) {
                (offset, maxOffset, newUpdates, user)
              } else {
                (None, None, Seq(), user)
              }
            }
          )

        for (u <- updates) {
          try {
            receiveUpdate(u, Some(user))
          } catch {
            case NonFatal(e) =>
              // Log and swallow, exception handling should happen on receiveUpdate.
              logger.error(s"receiveUpdate failed while processing: $u", e)
            case e: TelegramApiException =>
              logger.error(s"receiveUpdate failed while processing: $u", e)
              Seq.empty[Update]
          }
        }

      f
    }
  }

  private def startPolling(user: User): Future[Unit] = {
    logger.info(s"Starting (long) polling: timeout=$pollingTimeout seconds")
    polling = poll(Future.successful((None, None, Seq(), user))).map(_ => ())
    polling.onComplete {
      case _ => logger.info("Long polling terminated")
    }
    polling
  }

  override def run(): Future[Unit] = synchronized {
    if (polling != null) {
      throw new RuntimeException("Bot is already running")
    }
    for {
      deleted <- request(DeleteWebhook)
      if deleted
      getMe <- request(GetMe)
      p <- startPolling(getMe)
    } yield {
      p
    }
  }

  override def shutdown(): Unit = synchronized {
    if (polling == null) {
      throw new RuntimeException("Bot is not running")
    }
    super.shutdown()
    polling = null
  }
}
