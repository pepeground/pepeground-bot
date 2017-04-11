package com.pepeground.bot.services

import com.pepeground.bot.Config
import com.pepeground.bot.entities.{PairEntity, ReplyEntity, WordEntity}
import com.pepeground.bot.repositories.{PairRepository, WordRepository}
import scalikejdbc._

import scala.collection.mutable.ListBuffer
import scala.util.Random
import scala.util.control.Breaks._

class StoryService(words: List[String], context: List[String], chatId: Long, sentences: Option[Int] = None) {
  var currentSentences: ListBuffer[String] = ListBuffer()
  var currentWordIds: ListBuffer[Long] = ListBuffer()

  def generate(): Option[String] = {
    DB readOnly { implicit session =>
      currentWordIds = WordRepository.getByWords(words).map(_.id).to[ListBuffer]

      for ( a <- 0 to sentences.getOrElse(Random.nextInt(3)) ) {
        generateSentence()
      }
    }

    if (currentSentences.nonEmpty) {
      Some(currentSentences.mkString(" "))
    } else {
      None
    }
  }


  private def generateSentence()(implicit session: DBSession): Unit = {
    var sentence: ListBuffer[String] = ListBuffer()
    var safetyCounter = 50

    var firstWordId: Option[Long] = None
    var secondWordId: List[Option[Long]] = currentWordIds.map(Option(_)).toList

    var pair: Option[PairEntity] = None

    pair = PairRepository.getPairWithReplies(chatId, firstWordId, secondWordId.toList)

    breakable {
      while(true) {
        if ( safetyCounter < 0 ) break
        if ( pair.isEmpty ) break

        safetyCounter -= 1

        pair match {
          case Some(pe: PairEntity) =>
            val reply = Random.shuffle(pe.replies.sortBy(_.count).reverse.toList).headOption

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
                break
            }
          case None =>
            break
        }

        pair = PairRepository.getPairWithReplies(chatId, firstWordId, secondWordId)
      }
    }

    if (sentence.nonEmpty) {
      currentSentences += setSentenceEnd(sentence.mkString(" ").stripLineEnd)
    }
  }

  private def setSentenceEnd(s: String): String = {
    if(Config.bot.punctuation.endSentence.contains(s.takeRight(1))) {
      s
    } else {
      "%s%s".format(
        s,
        Config.bot.punctuation.endSentence(Random.nextInt(endSentenceLength))
      )
    }
  }

  lazy val endSentenceLength: Int = Config.bot.punctuation.endSentence.length
}