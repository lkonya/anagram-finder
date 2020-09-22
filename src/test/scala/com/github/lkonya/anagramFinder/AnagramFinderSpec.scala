package com.github.lkonya.anagramFinder

import java.io.IOException

import cats.data.NonEmptyChain
import com.github.lkonya.anagramFinder.FileHandler.FileHandler
import com.github.lkonya.anagramFinder.LookUpTable.LookUpTable
import com.github.lkonya.anagramFinder.model.Error.FileProcessError
import com.github.lkonya.anagramFinder.model.SortedLowerCasedString
import zio.blocking.Blocking
import zio.nio.core.file.Path
import zio.test.Assertion._
import zio.test._
import zio.test.environment.TestConsole
import zio.{UIO, ULayer, ZIO, ZLayer}

object AnagramFinderSpec extends DefaultRunnableSpec {

  val testFileHandlerSuccessful: ULayer[FileHandler] = ZLayer.succeed {
    new FileHandler.Service {
      override def loadFile(path: Path): ZIO[Blocking, FileProcessError, List[String]] =
        ZIO.succeed(List("SLIME", "slime", "slime1", "Slime1", "SLIME1", "slimebag"))
    }
  }

  val testFileHandlerFailure: ULayer[FileHandler] = ZLayer.succeed {
    new FileHandler.Service {
      override def loadFile(path: Path): ZIO[Blocking, FileProcessError, List[String]] =
        ZIO.fail(FileProcessError("Die", new IOException("Oh, snap")))
    }
  }

  val testLookUpTable: ULayer[LookUpTable] = ZLayer.succeed {
    new LookUpTable.Service {
      override def create(words: List[String]): UIO[Map[SortedLowerCasedString, NonEmptyChain[String]]] =
        ZIO.succeed(
          Map(
            SortedLowerCasedString("SLIME")    -> NonEmptyChain("SLIME", "slime"),
            SortedLowerCasedString("slime1")   -> NonEmptyChain("slime1", "Slime1", "SLIME1"),
            SortedLowerCasedString("slimebag") -> NonEmptyChain("slimebag")
          )
        )
    }
  }

  val testEnvSuccessful = testFileHandlerSuccessful and testLookUpTable and AnagramFinder.live

  val quitSignal = "q"

  override def spec =
    suite("AnagramFinderSpec")(
      testM("stops if " + quitSignal + "uit signal is provided") {
        for {
          _      <- TestConsole.feedLines(quitSignal)
          result <- AnagramFinder.findAnagram(Path("wordlist.txt"))
        } yield assert(result)(isUnit)
      }.provideCustomLayer(testEnvSuccessful),
      testM("print anagrams if word has anagrams") {
        val word   = "slime1"
        val output = s"Anagram(s) for '$word': slime1, Slime1, SLIME1\n"
        for {
          _        <- TestConsole.feedLines(word, quitSignal)
          _        <- AnagramFinder.findAnagram(Path("wordlist.txt"))
          printLns <- TestConsole.output
        } yield assert(printLns)(contains(output))
      }.provideCustomLayer(testEnvSuccessful),
      testM("print not found message if there is no anagram for word") {
        val word   = "asdfd"
        val output = s"No anagram found for '$word'.\n"
        for {
          _        <- TestConsole.feedLines(word, quitSignal)
          _        <- AnagramFinder.findAnagram(Path("wordlist.txt"))
          printLns <- TestConsole.output
        } yield assert(printLns)(contains(output))
      }.provideCustomLayer(testEnvSuccessful),
      testM("fail given file cannot be loaded") {
        assertM(AnagramFinder.findAnagram(Path("wordlist.txt")).run)(fails(isSubtype[FileProcessError](anything)))
      }.provideCustomLayer(testFileHandlerFailure and testLookUpTable and AnagramFinder.live)
    )

}
