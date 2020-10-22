// Renamed package according to sections "Packages and Imports" & "Creating
// a package".
package fmv1992.fmv1992_scala_utilities.uniq

import fmv1992.fmv1992_scala_utilities.util.Reader

import fmv1992.fmv1992_scala_utilities.cli.Argument
import fmv1992.fmv1992_scala_utilities.cli.CLIConfigTestableMain

/** Filter repeated lines independent of their position.
  *
  * The memory requirements are O(n).
  *
  * @see [[https://en.wikipedia.org/wiki/Uniq]]
  */
object Uniq extends CLIConfigTestableMain {

  val version = Reader.readLines("./src/main/resources/version").mkString

  val programName = "Uniq"

  val CLIConfigPath = "./src/main/resources/uniq_cli_config.conf"

  /** Testable interface for main program. */
  def testableMain(args: Seq[Argument]): Seq[String] = {

    // ???: This so common parsing should be responsibility of `main`.
    val (inputArgs, otherArgs) = splitInputArgumentFromOthers(args)
    val inputString = readInputArgument(inputArgs)

    val res = otherArgs
      .foldLeft(Seq.empty: Seq[String])((l, x) => {
        if (x.longName == "unique") {
          filterUnique(inputString)
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

  /** Filter unique elements. */
  def filterUnique[A](seq: Seq[A]): Seq[A] = {

    val set: Set[A] = Set.empty
    // By using a def we ensure that no references to the Steam exists.
    def uniqueSets: Stream[Set[A]] =
      set #:: Set(seq.head) #::
        uniqueSets.tail.zip(seq.tail).map(x => x._1 + x._2)
    seq.zip(uniqueSets).filter(x => !x._2.contains(x._1)).map(_._1)

  }
}
