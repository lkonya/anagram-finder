package anagram.model

class SortedCaseInsensitiveString private (private[model] val string: String) extends AnyVal

object SortedCaseInsensitiveString {

  def apply(string: String): SortedCaseInsensitiveString = new SortedCaseInsensitiveString(
    string.toLowerCase.toSeq.sorted.unwrap
  )

}
