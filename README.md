# Anagram-finder [![Tests Actions Status](https://github.com/lkonya/anagram-finder/workflows/CI/badge.svg)](https://github.com/lkonya/anagram-finder/actions)

## Prerequisites
This project requires at least Java 11 or newer. (The easiest way to get JDK 11 with [asdf](https://asdf-vm.com/#/core-manage-asdf) or [sdk](https://sdkman.io/install)))

You need to have [`sbt`](https://www.scala-sbt.org/download.html) installed and on your PATH.

A text file that contains the words you want to use to look up anagrams. 
If you don't have any, there is a text file under `src/main/resources/wordlist.txt`.

## Running tests
```scala
  sbt "test"
```

## Running main
Tha app requires the location of the text file that contains the words as first argument.
After reading the file and processing its content, it's ready to look up anagrams based on user's input.
If the user wants to quit, just give `q` as input.
```scala
  sbt "runMain com.github.lkonya.anagramFinder.AnagramFinderApp /location/of/the/wordlist"
```