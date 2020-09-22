package com.github.lkonya.anagramFinder

import cats.data.NonEmptyChain
import com.github.lkonya.anagramFinder.model.SortedLowerCasedString
import zio.test.Assertion.hasSameElements
import zio.test._
import zio.test.environment.TestEnvironment

object LookUpTableSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[TestEnvironment, Any] =
    suite("LookUpTableSpec")(
      testM("create the look up table") {
        val input = List("SLIME", "slime", "slime1", "Slime1", "SLIME1", "slimebag")
        val output = Map(
          SortedLowerCasedString("SLIME")    -> NonEmptyChain("SLIME", "slime"),
          SortedLowerCasedString("slime1")   -> NonEmptyChain("slime1", "Slime1", "SLIME1"),
          SortedLowerCasedString("slimebag") -> NonEmptyChain("slimebag")
        )

        assertM(LookUpTable.create(input))(hasSameElements(output))
      }
    ).provideCustomLayer(LookUpTable.live)

}
