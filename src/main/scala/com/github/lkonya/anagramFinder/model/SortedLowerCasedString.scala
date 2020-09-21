package com.github.lkonya.anagramFinder.model

class SortedLowerCasedString private (private[model] val string: String) extends AnyVal

object SortedLowerCasedString {

  def apply(string: String): SortedLowerCasedString = new SortedLowerCasedString(
    string.toLowerCase.toSeq.sorted.unwrap
  )

}
