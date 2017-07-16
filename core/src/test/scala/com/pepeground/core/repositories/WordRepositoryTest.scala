package com.pepeground.core.repositories

import org.flywaydb.core.Flyway
import scalikejdbc.scalatest.AutoRollback
import org.scalatest._
import org.scalatest.fixture.FlatSpec
import scalikejdbc.ConnectionPool
import scalikejdbc.config.DBs

class WordRepositoryTest extends FlatSpec with BeforeAndAfter with AutoRollback {
  before {
    DBs.setupAll()

    val flyway: Flyway = new Flyway()
    val dataSource = ConnectionPool.dataSource(ConnectionPool.DEFAULT_NAME)

    flyway.setDataSource(dataSource)
    flyway.baseline()
    flyway.migrate()
  }

  behavior of "create"

  it should "creates new word" in { implicit session =>
    val word = WordRepository.create("scala")

    assert(word.nonEmpty)
  }

  behavior of "getWordById"

  it should "return word by id" in { implicit session =>
    val word = WordRepository.create("scala")
    val sameWord = WordRepository.getWordById(word.get)

    assert(sameWord.nonEmpty)
    assert(sameWord.get.word == "scala")
  }

  behavior of "getByWord"

  it should "return word by string" in { implicit session =>
    val word = WordRepository.create("scala")

    val sameWord = WordRepository.getByWord("scala")

    assert(sameWord.nonEmpty)
    assert(word.get == sameWord.get.id)
  }

  behavior of "getByWords"

  it should "return list of words" in { implicit session =>
    val word1 = WordRepository.create("hello")
    val word2 = WordRepository.create("world")
    val word3 = WordRepository.create("scala")

    val wordIds = WordRepository.getByWords(List("hello", "world", "scala")).map(_.id)

    assert(wordIds == List(word1, word2, word3).map(_.get))
  }

  behavior of "learnWords"

  it should "learn new words" in { implicit session =>
    WordRepository.learWords(List("hello", "world", "scala"))

    val words = WordRepository.getByWords(List("hello", "world", "scala"))

    assert(words.size == 3)
  }

  it should "skip already learned words" in { implicit session =>
    val word2 = WordRepository.create("world")
    WordRepository.learWords(List("hello", "world", "scala"))

    val words = WordRepository.getByWords(List("hello", "world", "scala"))

    assert(words.size == 3)
  }
}