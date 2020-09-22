package com.github.lkonya.anagramFinder

import cats.data.NonEmptyChain
import com.github.lkonya.anagramFinder.FileHandler.FileHandler
import com.github.lkonya.anagramFinder.LookUpTable.LookUpTable
import com.github.lkonya.anagramFinder.model.Error.FileProcessError
import com.github.lkonya.anagramFinder.model.SortedLowerCasedString
import zio.blocking.Blocking
import zio.console.{getStrLn, putStrLn, Console}
import zio.nio.core.file.Path
import zio._

object AnagramFinder {
  type AnagramFinder = Has[AnagramFinder.Service]
  private val QuitSignal = "q"

  trait Service {

    def findAnagram(
        filePath: Path): ZIO[Console with Blocking with LookUpTable with FileHandler, FileProcessError, Unit]

  }

  val live: ULayer[AnagramFinder] = ZLayer.succeed {
    new Service {
      override def findAnagram(
          filePath: Path): ZIO[Console with Blocking with LookUpTable with FileHandler, FileProcessError, Unit] =
        for {
          _           <- putStrLn(s"Loading file '${filePath.filename}'.'")
          words       <- FileHandler.loadFile(filePath)
          _           <- putStrLn(s"File '${filePath.filename}' loaded, found ${words.length} word(s).")
          _           <- putStrLn("Processing words.")
          lookUpTable <- LookUpTable.create(words)
          _           <- mainLoop(lookUpTable)
        } yield ()

      private def mainLoop(lookUpTable: Map[SortedLowerCasedString, NonEmptyChain[String]]): URIO[Console, Unit] =
        for {
          _     <- putStrLn(s"Type in a word or '$QuitSignal' to quit.")
          input <- getStrLn.orDie
          _     <- (processInput(input, lookUpTable) *> mainLoop(lookUpTable)).when(input != QuitSignal)
        } yield ()

      private def processInput(
          word: String,
          lookUpTable: Map[SortedLowerCasedString, NonEmptyChain[String]]): URIO[Console, Unit] = {
        import cats.syntax.foldable._
        lookUpTable.get(SortedLowerCasedString(word)) match {
          case Some(anagrams) => putStrLn(s"Anagram(s) for '$word': ${anagrams.mkString_(", ")}")
          case None           => putStrLn(s"No anagram found for '$word'.")
        }
      }
    }
  }

  def findAnagram(path: Path)
      : ZIO[AnagramFinder with Console with Blocking with LookUpTable with FileHandler, FileProcessError, Unit] =
    ZIO.accessM(_.get[Service].findAnagram(path))

}
