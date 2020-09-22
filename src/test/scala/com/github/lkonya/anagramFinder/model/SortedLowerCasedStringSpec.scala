package com.github.lkonya.anagramFinder.model

import zio.test.Assertion._
import zio.test.environment.TestEnvironment
import zio.test.{DefaultRunnableSpec, ZSpec, _}

object SortedLowerCasedStringSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[TestEnvironment, Any] = suite("SortedLowerCasedStringTest")(
    test("create a string that is sorted") {
      assert(SortedLowerCasedString("asd").string)(equalTo("ads"))
    },
    test("create a string that is lower cased") {
      assert(SortedLowerCasedString("ASD").string)(equalTo("ads"))
    }
  )

}
