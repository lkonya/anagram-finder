package com.github.lkonya.anagramFinder.model

object Error {
  sealed abstract class AnagramError(message: String, original: Throwable) extends Throwable(message, original)
  case class FileProcessError(message: String, original: Throwable)        extends AnagramError(message, original)
}
