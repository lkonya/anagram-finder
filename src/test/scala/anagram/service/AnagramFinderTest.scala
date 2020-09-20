package anagram.service

import java.io.IOException
import java.nio.file.{Files, Path, Paths}

import anagram.model.SortedCaseInsensitiveString
import anagram.service.AnagramFinder.LookUpTable
import cats.data.NonEmptyChain
import cats.effect.concurrent.Ref
import cats.effect.{Blocker, Bracket, ContextShift, IO}
import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class AnagramFinderTest extends AnyWordSpecLike with Matchers with TypeCheckedTripleEquals {
  import cats.syntax.foldable._
  private val word                          = "he"
  private val anagrams                      = NonEmptyChain("eh", "eH", "Eh", "EH", "he", "hE", "He", "HE")
  private val lookUpTable                   = Map(SortedCaseInsensitiveString(word) -> anagrams)
  private val emptyLookUpTable: LookUpTable = Map.empty

  "lookUpAnagram" should {
    "return None when there is no match" in {
      implicit val emptyLookUpTableRef: Ref[IO, LookUpTable] = Ref.unsafe(Map.empty)

      AnagramFinder.lookUpAnagram[IO]("anyWord").unsafeRunSync() should ===(None)
    }

    "returns matched anagrams" in {
      implicit val lookUpTableRef: Ref[IO, LookUpTable] = Ref.unsafe(lookUpTable)

      AnagramFinder.lookUpAnagram[IO](word).unsafeRunSync() should ===(Some(anagrams))
    }
  }

  "createLookUpTable" should {

    "die if file doesn't exists" in {
      val result = Blocker[IO].use { blocker =>
        implicit val cs: ContextShift[IO] = IO.contextShift(blocker.blockingContext)
        AnagramFinder.createLookUpTable[IO](Paths.get("not", "existing"), blocker)
      }
      an[IOException] shouldBe thrownBy(result.unsafeRunSync().get.unsafeRunSync())
    }

    "create a lookuptable from file" in {
      val fileContent = anagrams.mkString_("\n")
      val result      = setup(fileContent)(implicit cs => AnagramFinder.createLookUpTable[IO])

      result.get.unsafeRunSync() should ===(lookUpTable)
    }

    "drop lines that are whitespaces" in {
      val fileContent = List("", " ", "\t", "\n").mkString_("\n")
      val result      = setup(fileContent)(implicit cs => AnagramFinder.createLookUpTable[IO])

      result.get.unsafeRunSync() should ===(emptyLookUpTable)
    }
  }

  private def setup[A](fileContent: String)(fa: ContextShift[IO] => (Path, Blocker) => IO[A]) =
    Blocker[IO]
      .use { blocker =>
        createTmpFile(fileContent)(path => fa(IO.contextShift(blocker.blockingContext))(path, blocker))
      }
      .unsafeRunSync()

  private def createTmpFile[A](fileContent: String)(fa: Path => IO[A]): IO[A] = {
    val acquire =
      IO(Files.createTempFile("AnagramFinderTest", "createLookUpTable"))
        .flatMap(path => IO(Files.writeString(path, fileContent)))
    def use(path: Path)     = fa(path)
    def release(path: Path) = IO(Files.delete(path))
    Bracket[IO, Throwable].bracket(acquire)(use)(release)
  }

}
