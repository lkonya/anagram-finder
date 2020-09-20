package anagram.model

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class SortedCaseInsensitiveStringTest extends AnyWordSpecLike with Matchers with TypeCheckedTripleEquals {
  "create a string that is sorted" in {
    SortedCaseInsensitiveString("asd").string should ===("ads")
  }
  "create a string that is lower cased" in {
    SortedCaseInsensitiveString("ASD").string should ===("ads")
  }
}
