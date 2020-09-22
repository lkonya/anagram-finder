package com.github.lkonya.anagramFinder

import java.io.IOException

import com.github.lkonya.anagramFinder.model.Error.FileProcessError
import zio.nio.core.file.Path
import zio.test.Assertion._
import zio.test._
import zio.test.environment.TestEnvironment

object FileHandlerSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[TestEnvironment, Any] =
    suite("FileHandlerTest")(
      testM("fail if file doesnt exist") {
        assertM(FileHandler.loadFile(Path("non_existing")).run)(fails(isSubtype[FileProcessError](anything)))
      },
      testM("load file") {
        val output = List("SLIME", "slime0", "slime1", "Slime1", "SLIME1", "slimebag", "slimebal", "slimeball")
        assertM(FileHandler.loadFile(Path("src/test/resources/wordlist.txt")))(hasSameElements(output))
      }
    ).provideCustomLayer(FileHandler.live)

}
