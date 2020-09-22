package com.github.lkonya.anagramFinder

import cats.data.NonEmptyChain
import com.github.lkonya.anagramFinder.model.SortedLowerCasedString
import zio._

import scala.annotation.tailrec
import scala.collection.mutable

object LookUpTable {
  type LookUpTable = Has[LookUpTable.Service]

  trait Service {
    def create(words: List[String]): UIO[Map[SortedLowerCasedString, NonEmptyChain[String]]]
  }

  val live: ULayer[LookUpTable] = ZLayer.succeed {
    new Service {

      override def create(words: List[String]): UIO[Map[SortedLowerCasedString, NonEmptyChain[String]]] =
        ZIO.effectTotal(createLookUpTable(words, mutable.HashMap.empty))

      @tailrec
      private def createLookUpTable(
          words: List[String],
          lookUpTable: mutable.HashMap[SortedLowerCasedString, NonEmptyChain[String]])
          : Map[SortedLowerCasedString, NonEmptyChain[String]] =
        words match {
          case word :: tail =>
            val key = SortedLowerCasedString(word)
            lookUpTable.get(key) match {
              case Some(words) => lookUpTable.update(key, words :+ word)
              case None        => lookUpTable.put(key, NonEmptyChain.one(word))
            }
            createLookUpTable(tail, lookUpTable)
          case Nil => lookUpTable.toMap
        }

    }
  }

  def create(words: List[String]): URIO[LookUpTable, Map[SortedLowerCasedString, NonEmptyChain[String]]] =
    ZIO.accessM(_.get.create(words))

}
