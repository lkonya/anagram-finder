package com.github.lkonya.anagramFinder.service

import cats.effect.Sync

trait Console[F[_]] {
  def getLine(): F[String]
  def putStrLn(s: String): F[Unit]
}

object Console {

  def apply[F[_]](implicit ev: Console[F]): Console[F] = ev

  implicit def syncInstance[F[_]: Sync]: Console[F] = new Console[F] {
    def getLine(): F[String]         = Sync[F].delay(scala.io.StdIn.readLine())
    def putStrLn(s: String): F[Unit] = Sync[F].delay(println(s))
  }

}
