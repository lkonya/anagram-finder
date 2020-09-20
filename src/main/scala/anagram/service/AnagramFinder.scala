package anagram.service

import java.io.{BufferedReader, File, FileReader}
import java.nio.file.Path

import anagram.model.SortedCaseInsensitiveString
import cats.data.NonEmptyChain
import cats.effect.concurrent.Ref
import cats.effect.{Blocker, ContextShift, Resource, Sync}

import scala.annotation.tailrec
import scala.collection.mutable

object AnagramFinder {
  import cats.syntax.flatMap._
  import cats.syntax.functor._

  type LookUpTable = Map[SortedCaseInsensitiveString, NonEmptyChain[String]]

  import scala.jdk.CollectionConverters._

  def lookUpAnagram[F[_]: Sync](word: String)(implicit
      lookUpTableRef: Ref[F, LookUpTable]): F[Option[NonEmptyChain[String]]] =
    lookUpTableRef.get.map(_.get(SortedCaseInsensitiveString(word)))

  def createLookUpTable[F[_]: Sync: ContextShift: Console](filePath: Path, blocker: Blocker): F[Ref[F, LookUpTable]] =
    for {
      _           <- Console[F].putStrLn(s"Starting to load '${filePath.getFileName}'.")
      words       <- readLinesFromFile(filePath.toFile, blocker)
      _           <- Console[F].putStrLn(s"'${filePath.getFileName}' loaded, ${words.length} word(s) found.")
      _           <- Console[F].putStrLn("Processing words.")
      lookUpTable <- Ref.of(createLookUpTableFrom(words))
    } yield lookUpTable

  private def readLinesFromFile[F[_]: Sync: ContextShift](file: File, blocker: Blocker): F[List[String]] =
    Resource
      .fromAutoCloseableBlocking(blocker)(Sync[F].delay(new BufferedReader(new FileReader(file))))
      .use(br => blocker.delay(br.lines().iterator().asScala.map(_.trim()).filter(_.nonEmpty).toList))

  private def createLookUpTableFrom(words: List[String]): LookUpTable = {
    @tailrec
    def createLookUpTable(
        words: List[String],
        lookUpTable: mutable.HashMap[SortedCaseInsensitiveString, NonEmptyChain[String]]): LookUpTable =
      words match {
        case word :: tail =>
          val key = SortedCaseInsensitiveString(word)
          lookUpTable.get(key) match {
            case Some(words) => lookUpTable.update(key, words :+ word)
            case None        => lookUpTable.put(key, NonEmptyChain(word))
          }
          createLookUpTable(tail, lookUpTable)
        case Nil => lookUpTable.toMap
      }

    createLookUpTable(words, mutable.HashMap.empty)
  }

}
