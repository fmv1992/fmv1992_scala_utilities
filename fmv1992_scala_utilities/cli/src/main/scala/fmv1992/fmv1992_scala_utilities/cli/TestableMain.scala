package fmv1992.fmv1992_scala_utilities.cli

import fmv1992.fmv1992_scala_utilities.util.Reader
import scala.Iterable

/** Testable main trait with a configurable file CLI implementation.
  *
  * Combine testability with modularity.
  */
trait CLIConfigTestableMain extends TestableMain {

  // ???: Due to Scala Native's limitations as of 2020-12-16, one cannot embed
  // files as resources. A workaround is to embed the string themselves,
  // perhaps wrapping around a `scala.io.Source.fromString` object.
  val CLIConfigPath: String

  val version: String

  val programName: String

  /** Get program version name.
    *
    * @see [[https://www.gnu.org/prep/standards/html_node/_002d_002dversion.html#g_t_002d_002dversion]]
    */
  def printVersion: Seq[String] = {
    List(s"$programName $version")
  }

  /** Get help string.
    *
    * @see [[https://www.gnu.org/prep/standards/html_node/_002d_002dhelp.html#g_t_002d_002dhelp]]
    */
  def printHelp(format: Map[String, Map[String, String]]): Seq[String] = {
    val usage: String = s"$programName " + format.keys.toList.sorted
      .map(x => "--" + x.format("name"))
      .mkString(" ")
    val description: String = format.keys.toList.sorted
      .map(x => " " * 4 + "--" + x.format("name") + ": " + format(x)("help"))
      .mkString("\n")
    Seq(usage, description)
  }

  /** Parse arguments, read stdin process and output to stdout.
    *
    * @see https://en.wikipedia.org/wiki/Standard_streams
    */
  def main(args: Array[String]): Unit = {
    val parser = GNUParser(CLIConfigPath)
    val parsed = parser.parse(args.toList)
    // Check if either version of help are given.
    val res = if (parsed.exists(_.longName == "help")) {
      printHelp(parser.format)
    } else if (parsed.exists(_.longName == "version")) {
      printVersion
    } else {
      testableMain(parsed)
    }
    res.foreach(println)
  }

}

// https://en.wikipedia.org/wiki/Standard_streams
// object StandardStreamMain {
//
// ???
//
// }

/** Provide a testable main interface: Read lines, process and output lines. */
trait TestableMain {

  def main(args: Array[String]): Unit

  // Need the command line arguments.
  // Does not need to specify the input stream (i.e. file or stdin). These
  // should be encoded by the parsed arguments.
  /** Testable interface for main program. */
  def testableMain(args: Seq[Argument]): Iterable[String]

  /** Split input [[Argument Arguments]] from other arguments. */
  def splitInputArgumentFromOthers(
      args: Seq[Argument]
  ): (Seq[Argument], Seq[Argument]) =
    splitArgumentFromOthers(args, "input")

  /** Split input [[Argument Arguments]] from other arguments. */
  def splitArgumentFromOthers(
      args: Seq[Argument],
      longName: String
  ): Tuple2[Seq[Argument], Seq[Argument]] = {

    def isXArg(x: Argument): Boolean = x.longName == longName

    val inputArg: Seq[Argument] = args.filter(isXArg)
    val otherArgs: Seq[Argument] = args.filterNot(isXArg(_))
    require(inputArg.length <= 1)

    (inputArg, otherArgs)

  }

  /** Read input argument (defaults to stdin). */
  def readInputArgument(a: Seq[Argument]): Seq[String] = {
    if (!a.isEmpty && a(0).value(0) != "null") {
      Reader.readLines(a(0).value(0))
    } else {
      scala.io.Source.stdin.getLines().toSeq
    }
  }

}
