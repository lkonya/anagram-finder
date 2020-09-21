import java.nio.file.{Path, Paths}

import cats.effect.concurrent.Ref
import cats.effect.{Blocker, ContextShift, ExitCode, IO, IOApp, Sync}
import com.github.lkonya.anagramFinder.service.AnagramFinder.LookUpTable
import com.github.lkonya.anagramFinder.service.{AnagramFinder, Console}

object AnagramFinderApp extends IOApp {
  import cats.syntax.applicative._
  import cats.syntax.flatMap._
  import cats.syntax.foldable._
  import cats.syntax.functor._

  private val QuitSignal = "q"

  override def run(args: List[String]): IO[ExitCode] =
    args match {
      case fileLocation :: _ =>
        Blocker[IO]
          .use(program[IO](Paths.get(fileLocation)))
          .as(ExitCode.Success)
          .handleErrorWith(error => Console[IO].putStrLn(error.toString).as(ExitCode.Error))
      case Nil =>
        Console[IO]
          .putStrLn("Please start the app by passing the words' location as first argument")
          .as(ExitCode.Error)
    }

  private def program[F[_]: Sync: ContextShift](filePath: Path)(blocker: Blocker): F[Unit] =
    for {
      implicit0(lookUpTable: Ref[F, LookUpTable]) <- AnagramFinder.createLookUpTable(filePath, blocker)
      _                                           <- mainLoop
    } yield ()

  private def mainLoop[F[_]: Sync: Console](implicit lookUpTable: Ref[F, LookUpTable]): F[Unit] =
    for {
      _     <- Console[F].putStrLn(s"Type in a word or '$QuitSignal' to quit")
      input <- Console[F].getLine()
      _     <- (processAnagram(input) >> mainLoop).unlessA(input == QuitSignal)
    } yield ()

  private def processAnagram[F[_]: Sync: Console](word: String)(implicit lookUpTable: Ref[F, LookUpTable]): F[Unit] =
    for {
      anagram <- AnagramFinder.lookUpAnagram(word)
      _ <- anagram match {
             case Some(anagrams) => Console[F].putStrLn(s"Anagram(s) for '$word': ${anagrams.mkString_(", ")}")
             case None           => Console[F].putStrLn(s"No anagram found for '$word'.")
           }
    } yield ()

}
