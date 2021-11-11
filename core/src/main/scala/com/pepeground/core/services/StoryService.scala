package com.pepeground.core.services

import com.pepeground.core.CoreConfig
import com.pepeground.core.entities.{PairEntity, ReplyEntity, WordEntity}
import com.pepeground.core.repositories.{PairRepository, ReplyRepository, WordRepository}
import scalikejdbc._

import scala.collection.mutable.ListBuffer
import scala.util.Random
import scala.util.control.Breaks._

class StoryService(words: List[String], context: List[String], chatId: Long, sentences: Option[Int] = None)(implicit session: DBSession) {
  var currentSentences: ListBuffer[String] = ListBuffer()
  var currentWordIds: ListBuffer[Long] = ListBuffer()
  //////////////////////////////////////////
  def generate(): Option[String] = {
      val currentWords: Map[String, Long] = WordRepository.getByWords(words ++ context).map(w => w.word -> w.id).toMap
      currentWordIds = words.map(w => currentWords.get(w)).filter(_.isDefined).map(_.get).to[ListBuffer]

      for ( a <- 0 to sentences.getOrElse(Random.nextInt(2) + 1) ) {
        generateSentence()
      }

    if (currentSentences.nonEmpty) {
      Some(currentSentences.mkString(" "))
    } else {
      None
    }
  }

//////////////////////////////////////////
  private def generateSentence(): Unit = {
    var sentence: ListBuffer[String] = ListBuffer()
    var safetyCounter = 50

    var firstWordId: Option[Long] = None
    var secondWordId: List[Option[Long]] = currentWordIds.map(Option(_)).toList

    var pair: Option[PairEntity] = None

    pair = Random.shuffle(PairRepository.getPairWithReplies(chatId, firstWordId, secondWordId)).headOption

    breakable {
      while(true) {
        if ( safetyCounter < 0 ) break
        if ( pair.isEmpty ) break

        safetyCounter -= 1

        pair match {
          case Some(pe: PairEntity) =>
            val reply = Random.shuffle(ReplyRepository.repliesForPair(pe.id)).headOption

            firstWordId = pe.secondId

            WordRepository.getWordById(pe.secondId.getOrElse(0.toLong)) match {
              case Some(we: WordEntity) =>
                if(sentence.isEmpty) {
                  sentence += we.word.toLowerCase
                  currentWordIds -= pe.secondId.getOrElse(0)
                }
              case None =>
            }

            reply match {
              case Some(re: ReplyEntity) => re.wordId match {
                case Some(wordId: Long) =>
                  secondWordId = List(re.wordId)

                  WordRepository.getWordById(wordId) match {
                    case Some(we: WordEntity) =>
                      sentence += we.word
                    case None =>
                      break
                  }
                case None =>
              }
              case None =>
            }
          case None =>
            break
        }

        pair = Random.shuffle(PairRepository.getPairWithReplies(chatId, firstWordId, secondWordId)).headOption
      }
    }

    if (sentence.nonEmpty) {
      currentSentences += setSentenceEnd(sentence.mkString(" ").stripLineEnd)
    }
  }

  private def setSentenceEnd(s: String): String = {
    if(CoreConfig.punctuation.endSentence.contains(s.takeRight(1))) {
      s
    } else {
      "%s%s".format(
        s,
        CoreConfig.punctuation.endSentence(Random.nextInt(endSentenceLength))
      )
    }
  }

  lazy val endSentenceLength: Int = CoreConfig.punctuation.endSentence.length
}