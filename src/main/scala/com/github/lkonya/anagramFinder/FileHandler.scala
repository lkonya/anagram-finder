package com.github.lkonya.anagramFinder

import java.nio.charset.StandardCharsets

import com.github.lkonya.anagramFinder.model.Error.FileProcessError
import zio.blocking.Blocking
import zio.nio.core.file.Path
import zio.nio.file.Files
import zio.{Has, ULayer, ZIO, ZLayer}

object FileHandler {
  type FileHandler = Has[FileHandler.Service]

  trait Service {
    def loadFile(path: Path): ZIO[Blocking, FileProcessError, List[String]]
  }

  val live: ULayer[FileHandler] = ZLayer.succeed {
    new Service {
      override def loadFile(path: Path): ZIO[Blocking, FileProcessError, List[String]] =
        Files
          .readAllLines(path, StandardCharsets.UTF_8)
          .catchAll(e =>
            ZIO.fail(FileProcessError("Please make sure you provide a path to an exisiting path with read rights", e))
          )
    }
  }

  def loadFile(path: Path): ZIO[Blocking with FileHandler, FileProcessError, List[String]] =
    ZIO.accessM(_.get[Service].loadFile(path))

}
