package com.github.lkonya.anagramFinder

import zio.console.putStrLn
import zio.nio.core.file.Path
import zio.{App, ExitCode, URIO, ZIO}

object Main extends App {

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] =
    args.headOption match {
      case Some(fileLocation) =>
        val program = for {
          path <- ZIO.effect(Path(fileLocation))
          _    <- AnagramFinder.findAnagram(path)
        } yield ()

        program
          .provideCustomLayer(FileHandler.live and LookUpTable.live and AnagramFinder.live)
          .exitCode
      case None =>
        putStrLn("Please start the app by passing the words' location as first argument")
          .as(ExitCode.failure)
    }

}
