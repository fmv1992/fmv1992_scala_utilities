// Renamed package according to sections "Packages and Imports" & "Creating
// a package".
package fmv1992.fmv1992_scala_utilities.uniq

import fmv1992.fmv1992_scala_utilities.util.Reader

import fmv1992.scala_cli_parser.CLIConfigTestableMain
import fmv1992.scala_cli_parser.Argument

/** Filter repeated lines independent of their position.
  *
  * The memory requirements are O(n).
  *
  * @see [[https://en.wikipedia.org/wiki/Uniq]]
  *
  */
object Uniq extends CLIConfigTestableMain {

  val version = Reader.readLines("./src/main/resources/version").mkString

  val programName = "Uniq"

  val CLIConfigPath = "./src/main/resources/uniq_cli_config.conf"

  /** Testable interface for main program. */
  def testableMain(args: Seq[Argument]): Seq[String] = {

    // ???: This so common parsing should be responsibility of `main`.
    val (inputArgs, otherArgs) = splitInputArgumentFromOthers(args)

    val res = otherArgs
      .foldLeft(Seq.empty: Seq[String])((l, x) ⇒ {
        if (x.longName == "unique") {
          filterUnique(readInputArgument(inputArgs).toStream)
        } else if (x.longName == "filter-adjacent") {
          throw new scala.NotImplementedError()
        } else {
          throw new scala.IllegalArgumentException()
        }
      })

    res.toSeq

  }

  // ???: Add trail of elidded lines.
  // See previous implementation on `comm69db9aa`.
  def recursiveFilterAdjacentMatchingRegex(
      nMax: Int,
      regex: String,
      iterator: Iterator[String]
  ): Iterator[String] = {
    ???
  }

  /** Filter unique elements.
    *
    * Also this Set stuff is bullshit:
    *
    * https://stackoverflow.com/a/6183115/5544140
    *
    * For the record:
    *
    * 1.  Stream approach (`comm9652bc5`).
    *
    * *     Pros: prints lines continuously.
    *
    * *     Cons: slow; StackOverflowError on ~5k lines.
    *
    * 2.  Tail recursive approach (`commc503cf2`):
    *
    * *     Pros: fast; has an upper bound for memory.
    *
    * *     Cons: Does not print lines continuously.
    *
    * 3.  Functional non tail recursive approach (`commbd7096d`):
    *
    * *     Pros: Prints lines continuously; fast.
    *
    * *     Cons: throws OutOfMemoryError.
    *
    * 4.  Imperative approach (`comm2fde2d2`):
    *
    * *     Pros: Prints lines continuously; fast.
    *
    * *     Cons: is imperative (?).
    *
    * Test version: `comm9652bc5`.
    *
    * */
  def filterUnique[A](inSeq: Seq[A]): Seq[A] = {

    def go(it: Iterator[A], s: Set[A]): Stream[A] = {
      if (it.hasNext) {
        val cur: A = it.next
        if (s.contains(cur)) {
          go(it, s)
        } else {
          Stream.cons(cur, go(it, s + cur))
        }
      } else {
        Stream.empty
      }
    }

    go(inSeq.iterator, Set.empty).toSeq
    //       ↑↑↑↑↑↑↑↑ Actually prevents "GC overhead limit exceeded".
  }

}
