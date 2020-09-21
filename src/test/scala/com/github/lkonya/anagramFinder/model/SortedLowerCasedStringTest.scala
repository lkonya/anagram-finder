package com.github.lkonya.anagramFinder.model

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class SortedLowerCasedStringTest extends AnyWordSpecLike with Matchers with TypeCheckedTripleEquals {
  "create a string that is sorted" in {
    SortedLowerCasedString("asd").string should ===("ads")
  }
  "create a string that is lower cased" in {
    SortedLowerCasedString("ASD").string should ===("ads")
  }
}
