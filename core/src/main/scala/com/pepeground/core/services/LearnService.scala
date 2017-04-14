package com.pepeground.core.services

import scalikejdbc._
import com.pepeground.core.CoreConfig
import com.pepeground.core.entities.{ReplyEntity, WordEntity}
import com.pepeground.core.repositories.{PairRepository, ReplyRepository, WordRepository}

import scala.collection.mutable.{Map => MMap}
import scala.collection.mutable.ListBuffer

class LearnService(words: List[String], chatId: Long) {
  def learnPair(): Unit = DB localTx { implicit session =>
    WordRepository.learWords(words)
    var newWords: ListBuffer[Option[String]] = ListBuffer(None)
    val preloadedWords: Map[String, WordEntity] = WordRepository.getByWords(words).map(we => we.word -> we).toMap

    words.foreach { w =>
      newWords += Option(w)

      if (CoreConfig.punctuation.endSentence.contains(w.takeRight(1))) newWords += None
    }

    newWords.last match {
      case Some(s: String) => newWords += None
      case _ =>
    }

    while(newWords.nonEmpty) {
      val trigramMap: MMap[Int, Long] = MMap()
      val trigram = newWords.take(3)
      newWords.remove(0, 1)

      trigram.zipWithIndex.foreach { case (w,i) =>
        w match {
          case Some(s: String) => preloadedWords.get(s) match {
            case Some(we: WordEntity) => trigramMap.put(i, we.id)
            case None =>
          }
          case None =>
        }
      }

      val pair = PairRepository.getPairOrCreateBy(chatId, trigramMap.get(0), trigramMap.get(1))

      ReplyRepository.getReplyBy(pair.id, trigramMap.get(2)) match {
        case Some(r: ReplyEntity) => ReplyRepository.incrementReply(r.id, r.count)
        case None => ReplyRepository.createReplyBy(pair.id, trigramMap.get(2))
      }
    }
  }
}